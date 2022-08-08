package com.agora.entfulldemo.models.room.live.holder;

import android.view.View;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.common.CenterCropRoundCornerTransform;
import com.agora.entfulldemo.common.GlideApp;
import com.agora.entfulldemo.databinding.KtvItemChooseSongListBinding;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.data.model.MusicModelNew;

/**
 * The holder of Item ChooseSong
 */
public class ChooseSongViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder<KtvItemChooseSongListBinding, MusicModelNew> {
    public ChooseSongViewHolder(@NonNull KtvItemChooseSongListBinding mBinding) {
        super(mBinding);
    }

    @Override
    public void binding(MusicModelNew data, int selectedIndex) {
        if (data != null) {
            mBinding.btnItemSongList.setOnClickListener(this::onItemClick);
            mBinding.titleItemSongList.setText(data.songName);
            mBinding.titleItemSongList.setOnLongClickListener(v -> {
                v.setSelected(!v.isSelected());
                return true;
            });
            mBinding.coverItemSongList.setVisibility(View.VISIBLE);
            GlideApp.with(itemView).load(data.imageUrl)
                    .transform(new CenterCropRoundCornerTransform(10))
                    .into(mBinding.coverItemSongList);
            if (RoomManager.getInstance().isInMusicOrderList(data)) {
                mBinding.btnItemSongList.setEnabled(false);
                mBinding.btnItemSongList.setText(R.string.ktv_room_choosed_song);
            } else {
                mBinding.btnItemSongList.setEnabled(true);
                mBinding.btnItemSongList.setText(R.string.ktv_room_choose_song);
            }
        }
    }
}