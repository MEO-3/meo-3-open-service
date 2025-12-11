package org.thingai.meo.callback;

public interface MRequestCallback<T> {
    void onSuccess(T result, String message);
    void onFailure(int errorCode, String errorMessage);
}
