package com.agora.entfulldemo.models.login.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.agora.entfulldemo.base.BaseViewBindingActivity;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.databinding.ActivityPhoneLoginBinding;
import com.agora.entfulldemo.dialog.SwipeCaptchaDialog;
import com.agora.entfulldemo.listener.OnButtonClickListener;
import com.agora.entfulldemo.manager.PagePathConstant;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.models.login.LoginViewModel;
import com.agora.entfulldemo.utils.CountDownTimerUtils;
import com.agora.entfulldemo.utils.SPUtil;
import com.agora.entfulldemo.utils.StringUtils;
import com.agora.entfulldemo.utils.ToastUtils;
import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * 登录注册
 */
@Route(path = PagePathConstant.pagePhoneLoginRegister)
public class PhoneLoginRegisterActivity extends BaseViewBindingActivity<ActivityPhoneLoginBinding> {

    /**
     * 登录模块统一ViewModel
     */
    private LoginViewModel phoneLoginViewModel;
    private SwipeCaptchaDialog swipeCaptchaDialog;

    /**
     * 记时器
     */
    private CountDownTimerUtils countDownTimerUtils;

    @Override
    protected ActivityPhoneLoginBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return ActivityPhoneLoginBinding.inflate(inflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        phoneLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        phoneLoginViewModel.setLifecycleOwner(this);
        phoneLoginViewModel.setISingleCallback((var1, var2) -> {
            if (var1 == KtvConstant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS) {
                RoomManager.getInstance().loginOut();
                PagePilotManager.pageMainHome();
                finish();
            }
            hideLoadingView();
        });
        setAccountStatus();
        String account = SPUtil.getString(KtvConstant.ACCOUNT, null);
        if (!TextUtils.isEmpty(account)) {
            getBinding().etAccounts.setText(account);
            String password = SPUtil.getString(KtvConstant.V_CODE, null);
            if (!TextUtils.isEmpty(password)) {
                getBinding().etVCode.setText(password);
            }
        }
        countDownTimerUtils = new CountDownTimerUtils(getBinding().tvSendVCode, 60000, 1000);
    }

    /**
     * 设置帐号输入框输入状态
     */
    private void setAccountStatus() {
        //手机号登录
        getBinding().etAccounts.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    }

    @Override
    protected boolean isCanExit() {
        return true;
    }

    @Override
    public void initListener() {
        getBinding().tvUserAgreement.setOnClickListener(view -> {
            PagePilotManager.pageWebView("https://www.agora.io/cn/about-us/");
        });
        getBinding().tvPrivacyAgreement.setOnClickListener(view -> {
            PagePilotManager.pageWebView("https://www.agora.io/cn/about-us/");
        });
        getBinding().btnLogin.setOnClickListener(view -> {
            if (getBinding().cvIAgree.isChecked()) {
                if (checkAccount()) {
                    showSwipeCaptchaDialog();
                }
            } else {
                ToastUtils.showToast("请同意我们的隐私政策与用户协议");
            }
        });
        getBinding().tvSendVCode.setOnClickListener(view -> {
            String account = getBinding().etAccounts.getText().toString();
            if (!StringUtils.checkPhoneNum(account)) {
                ToastUtils.showToast("请输入正确手机号");
            } else {
                phoneLoginViewModel.requestSendVCode(account);
                countDownTimerUtils.start();
            }
        });
        getBinding().iBtnClearAccount.setOnClickListener(view -> {
            getBinding().etAccounts.setText("");
        });
        getBinding().etAccounts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null && editable.length() > 0) {
                    getBinding().iBtnClearAccount.setVisibility(View.VISIBLE);
                } else {
                    getBinding().iBtnClearAccount.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showSwipeCaptchaDialog() {
        if (swipeCaptchaDialog == null) {
            swipeCaptchaDialog = new SwipeCaptchaDialog(this);
            swipeCaptchaDialog.setOnButtonClickListener(new OnButtonClickListener() {
                @Override
                public void onLeftButtonClick() {

                }

                @Override
                public void onRightButtonClick() {
                    //验证成功
                    showLoadingView();
                    String account = getBinding().etAccounts.getText().toString();
                    String vCode = getBinding().etVCode.getText().toString();
                    phoneLoginViewModel.requestLogin(account, vCode);
                    SPUtil.putString(KtvConstant.ACCOUNT, account);
                }
            });
        }
        swipeCaptchaDialog.show();

    }

    private boolean checkAccount() {
        String account = getBinding().etAccounts.getText().toString();
        if (!StringUtils.checkPhoneNum(account)) {
            ToastUtils.showToast("请输入正确手机号");
            return false;
        } else if (TextUtils.isEmpty(getBinding().etVCode.getText().toString())) {
            ToastUtils.showToast("请输入验证码");
        }
        return true;
    }
}
