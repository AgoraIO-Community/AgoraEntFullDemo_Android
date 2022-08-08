package com.agora.data.model;

public class KTVBaseResponse<T> {
    public int code;

    public String message;

    T data;

    public T getData() {
        return data;
    }
}
