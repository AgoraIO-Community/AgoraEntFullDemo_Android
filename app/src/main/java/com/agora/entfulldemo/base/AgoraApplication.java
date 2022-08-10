package com.agora.entfulldemo.base;

import android.app.Activity;
import android.content.res.Configuration;

import androidx.multidex.MultiDexApplication;

import com.agora.entfulldemo.agora.ChatManager;
import com.agora.entfulldemo.provider.DataRepositoryImpl2;
import com.agora.entfulldemo.utils.KTVUtil;
import com.agora.data.provider.DataRepository;
import com.alibaba.android.arouter.launcher.ARouter;
import com.elvishew.xlog.XLog;

import io.agora.rtm.RtmClient;
import io.reactivex.plugins.RxJavaPlugins;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.onAdaptListener;
import me.jessyan.autosize.utils.ScreenUtils;

public class AgoraApplication extends MultiDexApplication {
    private static AgoraApplication sInstance;
    private ChatManager mChatManager;
    private RtmClient mRtmClient;

    public static AgoraApplication the() {
        return sInstance;
    }

    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initARouter();
        initAutoSize();
        RxJavaPlugins.setErrorHandler(KTVUtil::logE);
        XLog.init();
        mChatManager = new ChatManager(this);
        mChatManager.init();
        mRtmClient = mChatManager.getRtmClient();
        DataRepository.Instance().setDataRepositoryImpl(new DataRepositoryImpl2());
    }

    private void initARouter() {
        ARouter.init(this);
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }

    public RtmClient getRtmClient() {
        return mRtmClient;
    }

    private void initAutoSize() {
        AutoSizeConfig.getInstance().setOnAdaptListener(new onAdaptListener() {
            @Override
            public void onAdaptBefore(Object o, Activity activity) {
                AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
                AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    AutoSizeConfig.getInstance()
                            .setDesignWidthInDp(812)
                            .setDesignHeightInDp(375);
                } else {
                    AutoSizeConfig.getInstance()
                            .setDesignWidthInDp(375)
                            .setDesignHeightInDp(812);
                }
            }

            @Override
            public void onAdaptAfter(Object o, Activity activity) {

            }
        });
    }
}