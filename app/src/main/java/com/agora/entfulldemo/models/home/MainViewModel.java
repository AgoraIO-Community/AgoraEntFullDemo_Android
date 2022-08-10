package com.agora.entfulldemo.models.home;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.entfulldemo.api.ApiException;
import com.agora.entfulldemo.api.ApiManager;
import com.agora.entfulldemo.api.ApiSubscriber;
import com.agora.entfulldemo.api.apiutils.SchedulersUtil;
import com.agora.entfulldemo.api.base.BaseResponse;
import com.agora.entfulldemo.api.model.User;
import com.agora.entfulldemo.base.BaseRequestViewModel;
import com.agora.entfulldemo.bean.CommonBean;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.event.UserInfoChangeEvent;
import com.agora.entfulldemo.event.UserLogoutEvent;
import com.agora.entfulldemo.manager.UserManager;
import com.agora.entfulldemo.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import io.reactivex.disposables.Disposable;

public class MainViewModel extends BaseRequestViewModel {
    @Override
    protected boolean isNeedEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@Nullable UserInfoChangeEvent event) {
        if (getISingleCallback() != null) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_USER_INFO_CHANGE, null);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(@Nullable UserLogoutEvent event) {
        if (getISingleCallback() != null) {
            getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_USER_LOGOUT, null);
        }
    }

    /**
     * 获取用户信息
     */
    public void requestUserInfo(String userNo) {
        ApiManager.getInstance().requestUserInfo(userNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        UserManager.getInstance().getUser().name = data.getData().name;
                        UserManager.getInstance().getUser().headUrl = data.getData().headUrl;
                        UserManager.getInstance().saveUserInfo(UserManager.getInstance().getUser());
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_LOGIN_REQUEST_LOGIN_SUCCESS, null);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    /**
     * 修改用户信息
     */
    public void requestEditUserInfo(String headUrl,
                                    String name,
                                    String sex) {
        ApiManager.getInstance().requestUserUpdate(headUrl, name, sex, UserManager.getInstance().getUser().userNo).
                compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<User>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<User> data) {
                        ToastUtils.showToast("修改成功");
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_USER_INFO_CHANGE, null);
                        if (!TextUtils.isEmpty(name)) {
                            UserManager.getInstance().getUser().name = name;
                        }
                        if (!TextUtils.isEmpty(headUrl)) {
                            UserManager.getInstance().getUser().headUrl = headUrl;
                        }
                        UserManager.getInstance().saveUserInfo(UserManager.getInstance().getUser());
                        requestUserInfo(UserManager.getInstance().getUser().userNo);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                });
    }

    public void updatePhoto(File file) {
        ApiManager.getInstance().requestUploadPhoto(file)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<CommonBean>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<CommonBean> data) {
                        UserManager.getInstance().getUser().headUrl = data.getData().url;
                        requestEditUserInfo(data.getData().url, null, null);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }

    /**
     * 注销用户
     */
    public void requestCancellation(String userNo) {
        ApiManager.getInstance().requestCancellationUser(userNo)
                .compose(SchedulersUtil.INSTANCE.applyApiSchedulers()).subscribe(
                new ApiSubscriber<BaseResponse<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDispose(d);
                    }

                    @Override
                    public void onSuccess(BaseResponse<String> data) {
                        getISingleCallback().onSingleCallback(KtvConstant.CALLBACK_TYPE_USER_CANCEL_ACCOUNTS, null);
                    }

                    @Override
                    public void onFailure(@Nullable ApiException t) {
                        ToastUtils.showToast(t.getMessage());
                    }
                }
        );
    }
}
