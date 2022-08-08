package com.agora.entfulldemo.models.room.live.fragment.dialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.agora.entfulldemo.R;
import com.agora.entfulldemo.base.BaseRecyclerViewAdapter;
import com.agora.entfulldemo.base.BaseViewBindingFragment;
import com.agora.entfulldemo.base.OnItemClickListener;
import com.agora.entfulldemo.databinding.FragmentDialogMvBinding;
import com.agora.entfulldemo.databinding.KtvItemMvBinding;
import com.agora.entfulldemo.manager.RoomManager;
import com.agora.entfulldemo.models.room.live.RoomLivingActivity;
import com.agora.entfulldemo.models.room.live.holder.MVHolder;
import com.agora.entfulldemo.widget.DividerDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MVFragment extends BaseViewBindingFragment<FragmentDialogMvBinding> implements OnItemClickListener<Integer> {
    private BaseRecyclerViewAdapter<KtvItemMvBinding, Integer, MVHolder> mAdapter;
    private int index;
    public static final List<Integer> exampleBackgrounds = new ArrayList<>(
            Arrays.asList(
                    R.mipmap.mvbg4,
                    R.mipmap.mvbg8,
                    R.mipmap.mvbg2,
                    R.mipmap.mvbg7,
                    R.mipmap.mvbg3,
                    R.mipmap.mvbg9,
                    R.mipmap.mvbg5,
                    R.mipmap.mvbg6,
                    R.mipmap.mvbg1

            ));

    public MVFragment(int index) {
        super();
        this.index = index;
    }

    @NonNull
    @Override
    protected FragmentDialogMvBinding getViewBinding(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup) {
        return FragmentDialogMvBinding.inflate(layoutInflater);
    }

    @Override
    public void initView() {
        mAdapter = new BaseRecyclerViewAdapter<>(exampleBackgrounds, this, MVHolder.class);
        mAdapter.selectedIndex = index;
        getBinding().rvList.setAdapter(mAdapter);
        getBinding().rvList.addItemDecoration(new DividerDecoration(3));
    }

    @Override
    public void initListener() {
        getBinding().ivBackIcon.setOnClickListener(view -> {
            ((RoomLivingActivity) requireActivity()).closeMenuDialog();
        });
    }

    @Override
    public void onItemClick(@NonNull Integer data, View view, int position, long viewType) {
        mAdapter.selectedIndex = position;
        RoomManager.mRoom.bgOption = String.valueOf(position);
        index = position;
        mAdapter.notifyDataSetChanged();
        ((RoomLivingActivity) requireActivity()).setPlayerBg(position);
    }
}
