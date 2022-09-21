package com.agora.entfulldemo.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.base.BaseDialog;
import com.agora.entfulldemo.databinding.DialogCloseRoomBinding;
import com.agora.entfulldemo.databinding.DialogCommonBinding;
import com.agora.entfulldemo.utils.KTVUtil;

public class CloseRoomDialog extends BaseDialog<DialogCloseRoomBinding> {
    public CloseRoomDialog(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected DialogCloseRoomBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return DialogCloseRoomBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        getBinding().btnRight.setOnClickListener(view -> {
            getOnButtonClickListener().onRightButtonClick();
            dismiss();
        });
    }

    @Override
    protected void setGravity() {
        getWindow().setLayout(
                KTVUtil.dp2px(300),
                KTVUtil.dp2px(120)
        );
        getWindow().getAttributes().gravity = Gravity.CENTER;
    }
}
