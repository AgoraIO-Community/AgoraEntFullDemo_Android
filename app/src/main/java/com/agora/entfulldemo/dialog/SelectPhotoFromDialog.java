package com.agora.entfulldemo.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseDialog;
import com.agora.entfulldemo.databinding.DialogSelectPhotoFromBinding;
import com.agora.entfulldemo.utils.KTVUtil;

/**
 * 选择相册或拍照 对话框
 */
public class SelectPhotoFromDialog extends BaseDialog<DialogSelectPhotoFromBinding> {
    public SelectPhotoFromDialog(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected DialogSelectPhotoFromBinding getViewBinding(@NonNull LayoutInflater inflater) {
        return DialogSelectPhotoFromBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        setCanceledOnTouchOutside(true);
        getWindow().setWindowAnimations(R.style.popup_window_style_bottom);
        getBinding().btnSelectFromAlbum.setOnClickListener(view -> {
            getOnButtonClickListener().onLeftButtonClick();
            dismiss();
        });
        getBinding().btnPhotograph.setOnClickListener(view -> {
            getOnButtonClickListener().onRightButtonClick();
            dismiss();
        });
        getBinding().btnCancel.setOnClickListener(view -> {
            dismiss();
        });
    }

    @Override
    protected void setGravity() {
        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                KTVUtil.dp2px(222)
        );
        getWindow().getAttributes().gravity = Gravity.BOTTOM;
    }
}
