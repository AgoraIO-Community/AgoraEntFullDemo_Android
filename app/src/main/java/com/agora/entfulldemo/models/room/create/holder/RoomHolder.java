package com.agora.entfulldemo.models.room.create.holder;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.common.CenterCropRoundCornerTransform;
import com.agora.entfulldemo.common.GlideApp;
import com.agora.entfulldemo.databinding.ItemRoomListBinding;
import com.agora.data.model.AgoraRoom;
import com.agora.entfulldemo.R;

public class RoomHolder extends BaseRecyclerViewAdapter.BaseViewHolder<ItemRoomListBinding, AgoraRoom> {

    public RoomHolder(@NonNull ItemRoomListBinding mBinding) {
        super(mBinding);
    }

    @Override
    public void binding(AgoraRoom data, int selectedIndex) {
        if (data != null) {
            GlideApp.with(mBinding.ivRoomCover.getContext()).load(data.getCoverRes())
                    .transform(new CenterCropRoundCornerTransform(40)).into(mBinding.ivRoomCover);
            mBinding.tvRoomName.setText(data.name);
            mBinding.tvPersonNum.setText(String.format("%d%s", data.roomPeopleNum, itemView.getContext().getString(R.string.people)));
            if (data.isPrivate == 1){
                mBinding.ivLock.setVisibility(View.VISIBLE);
            } else{
                mBinding.ivLock.setVisibility(View.GONE);
            }
        }
    }
}