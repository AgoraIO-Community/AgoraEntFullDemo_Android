package com.agora.entfulldemo.models.home.adapter.holder;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.bean.FindBean;
import com.agora.entfulldemo.databinding.ItemRoomListBinding;

public class FindHolder extends BaseRecyclerViewAdapter.BaseViewHolder<ItemRoomListBinding, FindBean> {

    public FindHolder(@NonNull ItemRoomListBinding mBinding) {
        super(mBinding);
    }

    @Override
    public void binding(FindBean data, int selectedIndex) {
        if (data != null) {
            
        }
    }
}