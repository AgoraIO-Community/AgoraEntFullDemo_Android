package com.agora.entfulldemo.models.webview;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseViewBindingActivity;
import com.agora.entfulldemo.common.KtvConstant;
import com.agora.entfulldemo.databinding.ActivityWebviewBinding;
import com.agora.entfulldemo.manager.PagePathConstant;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import kotlin.jvm.JvmField;

@Route(path = PagePathConstant.pageWebView)
public class WebViewActivity extends BaseViewBindingActivity<ActivityWebviewBinding> {
    /**
     * h5地址
     */
    @JvmField
    @Autowired(name = KtvConstant.URL)
    String url = "https://www.agora.io/cn/about-us/";


    @Override
    protected ActivityWebviewBinding getViewBinding(@NonNull LayoutInflater layoutInflater) {
        return ActivityWebviewBinding.inflate(layoutInflater);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        ARouter.getInstance().inject(this);
        if (url.contains("termsofuse")) {
            getBinding().titleView.setTitle(getString(R.string.user_agreement));
        } else if (url.contains("about-us")) {
            getBinding().titleView.setTitle(getString(R.string.about_us));
        } else if (url.contains("privacypolicy")){
            getBinding().titleView.setTitle(getString(R.string.privacy_agreement));
        }
        getBinding().webView.loadUrl(url);
    }
}
