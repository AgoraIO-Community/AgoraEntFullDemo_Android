package com.agora.entfulldemo.models.room.live.holder;

import android.view.View;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.common.GlideApp;
import com.agora.entfulldemo.databinding.KtvItemMvBinding;


/**
 * MVModel List
 */
public class MVHolder extends BaseRecyclerViewAdapter.BaseViewHolder<KtvItemMvBinding, Integer> {

    public MVHolder(@NonNull KtvItemMvBinding mBinding) {
        super(mBinding);
    }

    @Override
    public void binding(Integer data, int selectedIndex) {
        if (getAdapterPosition() == selectedIndex) {
            mBinding.ivSelected.setVisibility(View.VISIBLE);
        } else {
            mBinding.ivSelected.setVisibility(View.GONE);
        }
        GlideApp.with(mBinding.getRoot())
                .load(data)
                .into(mBinding.ivCover);
    }
}