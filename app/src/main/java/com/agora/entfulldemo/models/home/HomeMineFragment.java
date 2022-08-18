package com.agora.entfulldemo.models.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.agora.entfulldemo.BuildConfig;
import com.agora.entfulldemo.R;
import com.agora.entfulldemo.api.model.User;
import com.agora.entfulldemo.base.BaseViewBindingFragment;
import com.agora.entfulldemo.common.CenterCropRoundCornerTransform;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.common.GlideApp;
import com.agora.entfulldemo.databinding.FragmentHomeMineBinding;
import com.agora.entfulldemo.dialog.CommonDialog;
import com.agora.entfulldemo.dialog.EditNameDialog;
import com.agora.entfulldemo.dialog.SelectPhotoFromDialog;
import com.agora.entfulldemo.listener.OnButtonClickListener;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.manager.RTCManager;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.entfulldemo.utils.FileUtils;
import com.agora.entfulldemo.utils.ImageCompressUtil;
import com.agora.entfulldemo.utils.SPUtil;
import com.agora.entfulldemo.utils.UriUtils;

import java.io.File;

public class HomeMineFragment extends BaseViewBindingFragment<FragmentHomeMineBinding> {
    private CommonDialog logoutDialog;
    private CommonDialog logoffAccountDialog;
    private MainViewModel mainViewModel;
    private SelectPhotoFromDialog selectPhotoFromDialog;
    private EditNameDialog editNameDialog;

    @NonNull
    @Override
    protected FragmentHomeMineBinding getViewBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHomeMineBinding.inflate(inflater);
    }

    @Override
    public void initView() {
        String versionString = BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")";
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setLifecycleOwner(this);

        String sdkVersionString = RTCManager.getInstance().getAgoraRTCSdkVersion();
        getBinding().tvVersion.setText(getString(R.string.version_is, versionString, sdkVersionString));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initListener() {
        mainViewModel.setISingleCallback((type, o) -> {
            if (type == KtvConstant.CALLBACK_TYPE_USER_INFO_CHANGE) {
                User user = UserManager.getInstance().getUser();
                GlideApp.with(this).load(user.headUrl).error(R.mipmap.userimage)
                        .transform(new CenterCropRoundCornerTransform(100))
                        .into(getBinding().ivUserAvatar);
                getBinding().tvUserID.setText(getString(R.string.id_is_, user.userNo));
                getBinding().tvUserMobile.setText(user.name);
            } else if (type == KtvConstant.CALLBACK_TYPE_USER_CANCEL_ACCOUNTS) {
                UserManager.getInstance().logout();
                requireActivity().finish();
                PagePilotManager.pageWelcome();
            }
        });
        getBinding().tvUserAgreement.setOnClickListener(view -> {
            PagePilotManager.pageWebView("https://iot-console-web.sh.agoralab.co/terms/termsofuse");
        });
        getBinding().tvPrivacyAgreement.setOnClickListener(view -> {
            PagePilotManager.pageWebView("https://iot-console-web.sh.agoralab.co/terms/privacypolicy");
        });

        getBinding().tvLogout.setOnClickListener(view -> {
            showLogoutDialog();
        });
        getBinding().tvLogoffAccount.setOnClickListener(view -> {
            showLogoffAccountDialog();
        });
        getBinding().tvAbout.setOnClickListener(view -> {
            PagePilotManager.pageWebView("https://www.agora.io/cn/about-us/");
        });
        getBinding().vToEdit.setOnClickListener(view -> {
            if (editNameDialog == null) {
                editNameDialog = new EditNameDialog(getContext());
                editNameDialog.iSingleCallback = (type, o) -> {
                    if (type == 0) {
                        mainViewModel.requestEditUserInfo(null, (String) o, null);
                    }
                };
            }
            editNameDialog.show();
        });
        getBinding().ivUserAvatar.setOnClickListener(view -> {
            showSelectPhotoFromDialog();
        });
    }

    private static final int CHOOSE_PHOTO = 100;
    private static final int TAKE_PHOTO = 101;
    String mTempPhotoPath = null;

    private void showSelectPhotoFromDialog() {
        if (selectPhotoFromDialog == null) {
            selectPhotoFromDialog = new SelectPhotoFromDialog(getContext());
            selectPhotoFromDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    openAlbum();
                }

                @Override
                public void onRightButtonClick() {
                    takePhoto();
                }
            });
        }
        selectPhotoFromDialog.show();
    }

    private void openAlbum() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, CHOOSE_PHOTO);
    }

    private void takePhoto() {
        Intent intentToTakePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentToTakePhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File fileDir = new File(FileUtils.getTempSDPath());
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File photoFile = new File(fileDir, "photo.jpg");
        mTempPhotoPath = photoFile.getAbsolutePath();
        Uri imageUri = FileProvider.getUriForFile(
                getContext(),
                BuildConfig.APPLICATION_ID + ".fileProvider",
                photoFile);
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intentToTakePhoto, TAKE_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO) {
                Uri uri = data.getData();
                if (uri != null) {
                    String filePath = UriUtils.INSTANCE.getFilePathByUri(getContext(), uri);
                    if (!TextUtils.isEmpty(filePath)) {
                        setImage(filePath);
                    }
                }
            } else if (requestCode == TAKE_PHOTO) {
                setImage(mTempPhotoPath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(String filePath) {
        if (filePath == null)
            return;
        String path = ImageCompressUtil.displayPath(requireActivity(), filePath);
        if (TextUtils.isEmpty(path) || new File(filePath).length() <= 150000) {
            path = filePath;
        }
        mTempPhotoPath = path;
        // UserManager.getInstance().getUser().headUrl = mTempPhotoPath;
        mainViewModel.updatePhoto(new File(mTempPhotoPath));
    }

    @Override
    public void requestData() {
        mainViewModel.requestUserInfo(UserManager.getInstance().getUser().userNo);
    }

    private void showLogoffAccountDialog() {
        if (logoffAccountDialog == null) {
            logoffAccountDialog = new CommonDialog(requireContext());
            logoffAccountDialog.setDialogTitle("确定注销账号？");
            logoffAccountDialog.setDescText("注销账号后，您将暂时无法使用该账号体验我们的服务，真的要注销吗？");
            logoffAccountDialog.setDialogBtnText(getString(R.string.logoff), getString(R.string.cancel));
            logoffAccountDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    SPUtil.putBoolean(KtvConstant.IS_AGREE, false);
                    mainViewModel.requestCancellation(UserManager.getInstance().getUser().userNo);
                }

                @Override
                public void onRightButtonClick() {

                }
            });
        }
        logoffAccountDialog.show();
    }

    private void showLogoutDialog() {
        if (logoutDialog == null) {
            logoutDialog = new CommonDialog(requireContext());
            logoutDialog.setDialogTitle("确定退出登录吗？");
            logoutDialog.setDescText("退出登录后，我们还会继续保留您的账户数据，记得再来体验哦～");
            logoutDialog.setDialogBtnText(getString(R.string.exit), getString(R.string.cancel));
            logoutDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    SPUtil.putBoolean(KtvConstant.IS_AGREE, false);
                    UserManager.getInstance().logout();
                    requireActivity().finish();
                    PagePilotManager.pageWelcome();
                }

                @Override
                public void onRightButtonClick() {

                }
            });
        }
        logoutDialog.show();
    }
}
