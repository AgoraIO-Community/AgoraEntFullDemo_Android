package com.agora.entfulldemo.models.room.create.holder;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.common.CenterCropRoundCornerTransform;
import com.agora.entfulldemo.common.GlideApp;
import com.agora.entfulldemo.databinding.ItemRoomListBinding;
import com.agora.data.model.AgoraRoom;

public class RoomHolder extends BaseRecyclerViewAdapter.BaseViewHolder<ItemRoomListBinding, AgoraRoom> {

    public RoomHolder(@NonNull ItemRoomListBinding mBinding) {
        super(mBinding);
    }

    @Override
    public void binding(AgoraRoom data, int selectedIndex) {
        if (data != null) {
            GlideApp.with(mBinding.ivRoomCover.getContext()).load(data.getCoverRes())
                    .transform(new CenterCropRoundCornerTransform(20)).into(mBinding.ivRoomCover);
            mBinding.tvRoomName.setText(data.name);
            mBinding.tvPersonNum.setText(String.valueOf(data.roomPeopleNum));
        }
    }
}