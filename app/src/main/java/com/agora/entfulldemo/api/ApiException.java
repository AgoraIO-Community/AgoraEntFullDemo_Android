package com.agora.entfulldemo.api;

public class ApiException extends RuntimeException {
    public ApiException(int errCode, String msg) {
        super(msg);
    }
}
