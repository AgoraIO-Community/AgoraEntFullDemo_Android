package com.agora.entfulldemo.utils;

import android.widget.Toast;

import com.agora.entfulldemo.base.AgoraApplication;

public class ToastUtils {

    public static void showToast(int resStringId) {
        Toast.makeText(AgoraApplication.the(), resStringId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String str) {
        Toast.makeText(AgoraApplication.the(), str, Toast.LENGTH_SHORT).show();
    }
}
