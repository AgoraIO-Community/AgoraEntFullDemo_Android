package com.agora.entfulldemo.manager;

import android.text.TextUtils;

import com.agora.entfulldemo.api.ApiManager;
import com.agora.entfulldemo.api.model.User;
import com.agora.entfulldemo.base.AgoraApplication;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.event.UserInfoChangeEvent;
import com.agora.entfulldemo.utils.GsonUtil;
import com.agora.entfulldemo.utils.SPUtil;

import org.greenrobot.eventbus.EventBus;

public final class UserManager {
    private volatile static UserManager instance;
    private User mUser;

    private UserManager() {
    }

    public User getUser() {
        if (mUser != null) {
            if (TextUtils.isEmpty(ApiManager.token)) {
                ApiManager.token = mUser.token;
            }
            return mUser;
        }
        readingUserInfoFromPrefs();
        return mUser;
    }

    public void saveUserInfo(User user) {
        mUser = user;
        writeUserInfoToPrefs(false);
        EventBus.getDefault().post(new UserInfoChangeEvent());
    }

    public void logout() {
        writeUserInfoToPrefs(true);
    }

    private void writeUserInfoToPrefs(boolean isLogOut) {
        if (isLogOut) {
            mUser = null;
            SPUtil.putString(KtvConstant.CURRENT_USER, "");
        } else {
            SPUtil.putString(KtvConstant.CURRENT_USER, getUserInfoJson());
        }
    }

    private void readingUserInfoFromPrefs() {
        String userInfo = SPUtil.getString(KtvConstant.CURRENT_USER, "");
        if (!TextUtils.isEmpty(userInfo)) {
            mUser = GsonUtil.getInstance().fromJson(userInfo, User.class);
            if (TextUtils.isEmpty(ApiManager.token)) {
                ApiManager.token = mUser.token;
            }
        }
    }

    private String getUserInfoJson() {
        return GsonUtil.getInstance().toJson(mUser);
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class) {
                if (instance == null)
                    instance = new UserManager();
            }
        }
        return instance;
    }

    public boolean isLogin() {
        if (mUser == null) {
            readingUserInfoFromPrefs();
            return mUser != null && !TextUtils.isEmpty(mUser.userNo);
        } else {
            if (TextUtils.isEmpty(ApiManager.token)) {
                ApiManager.token = mUser.token;
            }
        }
        return true;
    }

}
