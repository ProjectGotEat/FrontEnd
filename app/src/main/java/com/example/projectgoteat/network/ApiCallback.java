package com.example.projectgoteat.network;

public interface ApiCallback<T> {
    void onSuccess(T response);
    void onFailure(String errorMessage);
}
