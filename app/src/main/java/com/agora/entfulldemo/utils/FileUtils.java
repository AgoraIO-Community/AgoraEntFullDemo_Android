package com.agora.entfulldemo.utils;

import com.agora.entfulldemo.base.AgoraApplication;

import java.io.File;

public class FileUtils {
    public static String getBaseStrPath() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
//        } else {
        return AgoraApplication.the().getExternalFilesDir("media").getAbsolutePath() + File.separator;
//        }
    }

    public static String getTempSDPath() {
        return getBaseStrPath() + "ag" + File.separator;
    }

}
