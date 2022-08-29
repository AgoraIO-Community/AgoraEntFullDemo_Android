package com.agora.entfulldemo.dialog;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseDialog;
import com.agora.entfulldemo.databinding.DialogUserAgreementBinding;
import com.agora.entfulldemo.manager.PagePilotManager;
import com.agora.entfulldemo.utils.KTVUtil;

public class UserAgreementDialog extends BaseDialog<DialogUserAgreementBinding> {
    public UserAgreementDialog(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected DialogUserAgreementBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return DialogUserAgreementBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        setCancelable(false);
        getBinding().btnDisagree.setOnClickListener(view -> {
            getOnButtonClickListener().onLeftButtonClick();
        });

        ClickableSpan protocolClickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                PagePilotManager.pageWebView("https://beidou-releases.oss-cn-zhangjiakou.aliyuncs.com/agora/static/user_agreement.html");
            }
        };
        ClickableSpan protocolClickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                PagePilotManager.pageWebView("https://beidou-releases.oss-cn-zhangjiakou.aliyuncs.com/agora/static/privacy_policy.html");
            }
        };
        ClickableSpan protocolClickableSpan3 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                PagePilotManager.pageWebView("https://beidou-releases.oss-cn-zhangjiakou.aliyuncs.com/agora/static/user_agreement.html");
            }
        };
        ClickableSpan protocolClickableSpan4 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                PagePilotManager.pageWebView("https://beidou-releases.oss-cn-zhangjiakou.aliyuncs.com/agora/static/privacy_policy.html");
            }
        };


        ForegroundColorSpan spanColor = new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.blue_9F));
        SpannableStringBuilder spannable = new SpannableStringBuilder(getContext().getString(R.string.privacy_protection_tip1));
        spannable.setSpan(protocolClickableSpan1, 72, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(spanColor, 72, 76, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(protocolClickableSpan2, 79, 83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(spanColor, 79, 83, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(protocolClickableSpan3, 202, 207, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(spanColor, 202, 207, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        spannable.setSpan(protocolClickableSpan4, 210, 214, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannable.setSpan(spanColor, 210, 214, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        getBinding().tvProtection.setText(spannable);
        getBinding().btnAgree.setOnClickListener(view -> {
            getOnButtonClickListener().onRightButtonClick();
        });
        getBinding().tvProtection.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void setGravity() {
        getWindow().setLayout(
                KTVUtil.dp2px(280),
                KTVUtil.dp2px(365)
        );
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }
}
