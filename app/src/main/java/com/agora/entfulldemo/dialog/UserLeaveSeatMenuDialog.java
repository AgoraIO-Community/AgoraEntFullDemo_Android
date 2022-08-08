package com.agora.entfulldemo.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.databinding.KtvDialogUserSeatMenuBinding;
import com.agora.baselibrary.base.BaseDialog;
import com.agora.baselibrary.utils.ScreenUtils;
import com.agora.data.model.AgoraMember;
import com.bumptech.glide.Glide;

/**
 * 房间用户菜单
 */
public class UserLeaveSeatMenuDialog extends BaseDialog<KtvDialogUserSeatMenuBinding> {
    public UserLeaveSeatMenuDialog(@NonNull Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected KtvDialogUserSeatMenuBinding getViewBinding(@NonNull LayoutInflater layoutInflater) {
        return KtvDialogUserSeatMenuBinding.inflate(layoutInflater);
    }

    public void setAgoraMember(AgoraMember agoraMember) {
        getBinding().tvName.setText(agoraMember.name);
        Glide.with(getContext())
                .load(agoraMember.headUrl).error(R.mipmap.userimage)
                .into(getBinding().ivUser);
    }

    @Override
    public void initView() {
        setCanceledOnTouchOutside(true);
        getWindow().setWindowAnimations(R.style.popup_window_style_bottom);
        getBinding().btSeatoff.setOnClickListener(this::seatOff);
    }

    private void seatOff(View v) {
        if (getOnButtonClickListener() != null) {
            dismiss();
            getOnButtonClickListener().onRightButtonClick();
        }
    }

    @Override
    protected void setGravity() {
        getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtils.dp2px(220)
        );
        getWindow().getAttributes().gravity = Gravity.BOTTOM;
    }
}
