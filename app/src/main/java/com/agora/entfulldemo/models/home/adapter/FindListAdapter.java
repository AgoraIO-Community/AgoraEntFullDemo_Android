package com.agora.entfulldemo.models.home.adapter;

import androidx.annotation.NonNull;

import com.agora.entfulldemo.R;
import com.agora.baselibrary.base.BaseAdapter;
import com.agora.data.model.AgoraRoom;

import java.util.ArrayList;

public class FindListAdapter extends BaseAdapter<AgoraRoom> {
    @Override
    public int getLayoutId(int i) {
        return R.layout.item_room_list;
    }

    public FindListAdapter(ArrayList<AgoraRoom> rooms) {
        super(rooms);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        AgoraRoom room = getDatas().get(position);
        if (room != null) {

        }
    }


}
