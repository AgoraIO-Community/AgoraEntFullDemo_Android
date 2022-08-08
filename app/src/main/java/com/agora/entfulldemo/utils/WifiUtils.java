package com.agora.entfulldemo.utils;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.appcompat.app.AppCompatActivity;

import com.agora.entfulldemo.base.AgoraApplication;

public class WifiUtils {
    public static int getWifiStatus() {
        WifiManager wifi_service = (WifiManager) AgoraApplication.mInstance.getSystemService(AppCompatActivity.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi_service.getConnectionInfo();
        return wifiInfo.getRssi();
    }
}
