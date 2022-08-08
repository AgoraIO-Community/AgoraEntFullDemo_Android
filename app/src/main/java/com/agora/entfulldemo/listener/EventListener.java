package com.agora.entfulldemo.listener;

import com.agora.data.sync.AgoraException;

public interface EventListener {
    void onSuccess();

    void onReceive();

    void onError(String error);

    void onSubscribeError(AgoraException ex);
}