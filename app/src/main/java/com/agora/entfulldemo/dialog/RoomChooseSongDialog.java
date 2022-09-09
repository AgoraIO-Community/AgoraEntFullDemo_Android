package com.agora.entfulldemo.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseBottomSheetDialogFragment;
import com.agora.entfulldemo.databinding.KtvDialogChooseSongBinding;
import com.agora.entfulldemo.event.MusicListChangeEvent;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.models.room.live.RoomLivingActivity;
import com.agora.entfulldemo.models.room.live.fragment.SongOrdersFragment;
import com.agora.entfulldemo.models.room.live.fragment.SongsFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 点歌菜单
 */
public class RoomChooseSongDialog extends BaseBottomSheetDialogFragment<KtvDialogChooseSongBinding> {
    public static final String TAG = RoomChooseSongDialog.class.getSimpleName();

    public static boolean isChorus = false;

    public RoomChooseSongDialog(boolean isChorus) {
        RoomChooseSongDialog.isChorus = isChorus;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewCompat.setOnApplyWindowInsetsListener(requireDialog().getWindow().getDecorView(), (v, insets) -> {
            Insets inset = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            mBinding.pager.setPadding(0, 0, 0, inset.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        mBinding.rBtnChooseSong.setChecked(true);
        mBinding.pager.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        mBinding.pager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(),
                getViewLifecycleOwner().getLifecycle()) {

            @Override
            public int getItemCount() {
                return 2;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return new SongsFragment();
                } else {
                    return new SongOrdersFragment();
                }
            }
        });
        mBinding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    mBinding.rBtnChooseSong.setChecked(true);
                } else {
                    mBinding.rBtnChorus.setChecked(true);
                }
            }
        });
        mBinding.radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rBtnChooseSong) {
                mBinding.pager.setCurrentItem(0);
            } else {
                mBinding.pager.setCurrentItem(1);
            }
        });
        onEventMainThread(null);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MusicListChangeEvent event) {
        int count = RoomManager.getInstance().getMusics().size();
        if (count > 0) {
            mBinding.tvChoosedSongCount.setVisibility(View.VISIBLE);
            if (count > 99) {
                count = 99;
            }
            mBinding.tvChoosedSongCount.setText(String.valueOf(count));
        } else {
            mBinding.tvChoosedSongCount.setVisibility(View.GONE);
        }
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        ((RoomLivingActivity) requireActivity()).setDarkStatusIcon(false);
    }
}
