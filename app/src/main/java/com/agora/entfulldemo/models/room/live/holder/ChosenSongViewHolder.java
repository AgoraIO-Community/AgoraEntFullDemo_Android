package com.agora.entfulldemo.models.room.live.holder;

import android.view.View;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.bean.MemberMusicModel;
import com.agora.entfulldemo.common.CenterCropRoundCornerTransform;
import com.agora.entfulldemo.common.GlideApp;
import com.agora.entfulldemo.databinding.KtvItemChoosedSongListBinding;
import com.agora.entfulldemo.manager.RoomManager;

/**
 * The holder of Item ChooseSong
 */
public class ChosenSongViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder<KtvItemChoosedSongListBinding, MemberMusicModel> {
    public ChosenSongViewHolder(@NonNull KtvItemChoosedSongListBinding mBinding) {
        super(mBinding);
    }

    @Override
    public void binding(MemberMusicModel item, int selectedIndex) {
        if (item != null) {
            mBinding.tvNo.setText(String.valueOf(getAdapterPosition() + 1));
            mBinding.tvMusicName.setText(item.songName);
            GlideApp.with(itemView).load(item.imageUrl)
                    .transform(new CenterCropRoundCornerTransform(10))
                    .into(mBinding.ivCover);
            if (getAdapterPosition() == 0) {
                mBinding.tvSing.setVisibility(View.VISIBLE);
                mBinding.ivToDel.setVisibility(View.GONE);
                mBinding.ivToTop.setVisibility(View.GONE);
            } else if (getAdapterPosition() == 1 && RoomManager.getInstance().getMine().isMaster) {
                mBinding.tvSing.setVisibility(View.GONE);
                mBinding.ivToDel.setVisibility(View.VISIBLE);
                mBinding.ivToTop.setVisibility(View.GONE);
            } else if (RoomManager.getInstance().getMine().isMaster) {
                mBinding.tvSing.setVisibility(View.GONE);
                mBinding.ivToDel.setVisibility(View.VISIBLE);
                mBinding.ivToTop.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvSing.setVisibility(View.GONE);
                mBinding.ivToDel.setVisibility(View.GONE);
                mBinding.ivToTop.setVisibility(View.GONE);
            }
            if (item.isChorus) {
                mBinding.tvChorus.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvChorus.setVisibility(View.GONE);
            }
            mBinding.ivToDel.setOnClickListener(view -> onItemClick(view));
            mBinding.tvChooser.setText(itemView.getContext().getString(R.string.song_ordering_person, item.name));
        }
    }
}