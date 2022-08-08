package com.agora.entfulldemo.models.welcome;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.agora.entfulldemo.base.BaseViewBindingActivity;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.databinding.ActivityWelcomeBinding;
import com.agora.entfulldemo.dialog.UserAgreementDialog;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.baselibrary.base.BaseDialog;
import com.agora.baselibrary.utils.SPUtil;

public class WelcomeActivity extends BaseViewBindingActivity<ActivityWelcomeBinding> {
    private UserAgreementDialog userAgreementDialog;

    @Override
    protected ActivityWelcomeBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityWelcomeBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
    }

    @Override
    public void initListener() {
    }

    @Override
    public boolean isBlackDarkStatus() {
        return false;
    }

    /**
     * 显示用户协议 隐私政策对话框
     */
    private void showUserAgreementDialog() {
        if (userAgreementDialog == null) {
            userAgreementDialog = new UserAgreementDialog(this);
            userAgreementDialog.setOnButtonClickListener(new BaseDialog.OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    userAgreementDialog.dismiss();
                    mHealthActivityManager.popActivity();
                }

                @Override
                public void onRightButtonClick() {
                    startMainActivity();
                    userAgreementDialog.dismiss();
                    SPUtil.Companion.getInstance(WelcomeActivity.this).putBoolean(KtvConstant.IS_AGREE, true);
                }
            });
        }
        userAgreementDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void getPermissions() {
        startMainActivity();
    }

    private void startMainActivity() {
        if (UserManager.getInstance().isLogin()) {
            PagePilotManager.pageMainHome();
        } else {
            PagePilotManager.pagePhoneLoginRegister();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHealthActivityManager.finishActivityByClass("WelcomeActivity");
    }

}
