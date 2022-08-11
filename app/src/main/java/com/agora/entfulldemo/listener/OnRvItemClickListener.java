package com.agora.entfulldemo.listener;

import android.view.View;

public interface OnRvItemClickListener<T> {
    void onItemClick(View view,
                     int position,
                     T data);
}
