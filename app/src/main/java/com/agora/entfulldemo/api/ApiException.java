package com.agora.entfulldemo.api;

public class ApiException extends RuntimeException {
    public int errCode;
    public ApiException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }
}
