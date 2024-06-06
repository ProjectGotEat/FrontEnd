package com.example.projectgoteat.api;

public interface ApiCallback<T> {
    void onSuccess(T response);
    void onFailure(String errorMessage);
}