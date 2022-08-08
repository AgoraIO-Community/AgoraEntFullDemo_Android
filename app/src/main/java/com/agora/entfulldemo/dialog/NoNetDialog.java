package com.agora.entfulldemo.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.databinding.DialogNoNetBinding;
import com.agora.baselibrary.base.BaseDialog;
import com.agora.baselibrary.utils.ScreenUtils;

public class NoNetDialog extends BaseDialog<DialogNoNetBinding> {
    public NoNetDialog(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected DialogNoNetBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return DialogNoNetBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        getBinding().btnHasKnow.setOnClickListener(view -> {
            dismiss();
        });
    }


    @Override
    protected void setGravity() {
        getWindow().setLayout(
                ScreenUtils.dp2px(295),
                ScreenUtils.dp2px(276)
        );
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }
}
