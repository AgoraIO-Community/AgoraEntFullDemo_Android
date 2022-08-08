package com.agora.entfulldemo.models.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.entfulldemo.api.ApiException;
import com.agora.entfulldemo.api.ApiManager;
import com.agora.entfulldemo.api.ApiSubscriber;
import com.agora.entfulldemo.api.apiutils.SchedulersUtil;
import com.agora.entfulldemo.api.base.BaseResponse;
import com.agora.entfulldemo.api.model.User;
import com.agora.entfulldemo.base.BaseRequestViewModel;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.baselibrary.utils.ToastUtils;

import io.reactivex.disposables.Disposable;

public class LoginViewModel extends BaseRequestViewModel {

    /**
     * 登录
     *
     * @param account 账号
     * @param vCode   验证码
     */
    public void requestLogin(String account, String vCode) {
        if (!account.equals(phone)) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_FAIL, null);
            ToastUtils.INSTANCE.showToast("验证码错误");
            return;
        }
        ApiManager.getInstance().requestLogin(account, vCode)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        ToastUtils.INSTANCE.showToast("登录成功");
                        ApiManager.token = (data.getData().token);
                        UserManager.getInstance().saveUserInfo(data.getData());
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS, null);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.INSTANCE.showToast(t.getMessage());
                    }
                }
        );
    }

    private String phone;

    /**
     * 发送验证码
     *
     * @param phone 手机号
     */
    public void requestSendVCode(String phone) {
        this.phone = phone;
        ApiManager.getInstance().requestSendVerCode(phone)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> stringBaseResponse) {
                        ToastUtils.INSTANCE.showToast("验证码发送成功");
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.INSTANCE.showToast(t.getMessage());
                    }
                }
        );
    }
}
