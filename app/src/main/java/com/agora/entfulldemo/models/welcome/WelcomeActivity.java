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
import com.agora.entfulldemo.listener.OnButtonClickListener;
import com.agora.entfulldemo.manager.PagePathConstant;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.entfulldemo.utils.SPUtil;
import com.alibaba.android.arouter.facade.annotation.Route;

@Route(path = PagePathConstant.pageWelcome)
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
            userAgreementDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {
                    userAgreementDialog.dismiss();
                    finish();
                }

                @Override
                public void onRightButtonClick() {
                    PagePilotManager.pagePhoneLoginRegister();
                    userAgreementDialog.dismiss();
                }
            });
        }
        userAgreementDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void getPermissions() {
        checkStatusToStart();
    }

    private void checkStatusToStart() {
        startMainActivity();
    }

    private void startMainActivity() {
        if (UserManager.getInstance().isLogin()) {
            PagePilotManager.pageMainHome();
        } else {
            if (!SPUtil.getBoolean(KtvConstant.IS_AGREE, false)) {
                showUserAgreementDialog();
            } else {
                PagePilotManager.pagePhoneLoginRegister();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

}
