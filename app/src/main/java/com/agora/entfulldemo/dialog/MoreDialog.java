package com.agora.entfulldemo.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseBottomSheetDialogFragment;
import com.agora.entfulldemo.base.BaseViewBindingFragment;
import com.agora.entfulldemo.bean.MusicSettingBean;
import com.agora.entfulldemo.databinding.KtvDialogMoreBinding;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.models.room.live.RoomLivingActivity;
import com.agora.entfulldemo.models.room.live.fragment.dialog.BeautyVoiceFragment;
import com.agora.entfulldemo.models.room.live.fragment.dialog.EffectVoiceFragment;
import com.agora.entfulldemo.models.room.live.fragment.dialog.MVFragment;
import com.agora.entfulldemo.utils.ToastUtils;
import com.agora.data.model.AgoraMember;

public class MoreDialog extends BaseBottomSheetDialogFragment<KtvDialogMoreBinding> {
    public static final String TAG = "MoreDialog";

    private final MusicSettingBean mSetting;

    public MoreDialog(MusicSettingBean mSetting) {
        this.mSetting = mSetting;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Edge to edge
        ViewCompat.setOnApplyWindowInsetsListener(requireDialog().getWindow().getDecorView(), (v, insets) -> {
            Insets inset = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            mBinding.getRoot().setPadding(inset.left, 0, inset.right, inset.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        initView();
    }

    private void initView() {
        AgoraMember mMine = RoomManager.getInstance().getMine();
        if (mMine == null) {
            ToastUtils.showToast("Self AgoraMember is null");
            dismiss();
        } else {
            mBinding.iBtnMV.setImageResource(mMine.isVideoMuted == 0 ? R.mipmap.ic_camera_on : R.mipmap.ic_camera_off);
            mBinding.iBtnBeautyVoice.setOnClickListener(this::showVoicePage);
            mBinding.iBtnEffectVoice.setOnClickListener(this::showEffectPage);
            mBinding.iBtnMV.setOnClickListener(this::showMVPage);
        }
    }

    private void showVoicePage(View v) {
        mBinding.getRoot().removeAllViews();
        BaseViewBindingFragment<?> voiceFragment = new BeautyVoiceFragment(mSetting);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(mBinding.getRoot().getId(), voiceFragment, BeautyVoiceFragment.TAG);
        ft.commit();
    }

    private void showEffectPage(View v) {
        mBinding.getRoot().removeAllViews();
        BaseViewBindingFragment<?> voiceFragment = new EffectVoiceFragment(mSetting);
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(mBinding.getRoot().getId(), voiceFragment, EffectVoiceFragment.TAG);
        ft.commit();
    }

    private void showMVPage(View v) {
        mBinding.getRoot().removeAllViews();
        BaseViewBindingFragment<?> voiceFragment;
        if (TextUtils.isEmpty(RoomManager.getInstance().getRoom().bgOption)) {
            voiceFragment = new MVFragment(0);
        } else {
            voiceFragment = new MVFragment(Integer.parseInt(RoomManager.getInstance().getRoom().bgOption));
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(mBinding.getRoot().getId(), voiceFragment, EffectVoiceFragment.TAG);
        ft.commit();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((RoomLivingActivity) requireActivity()).setDarkStatusIcon(false);
    }
}
