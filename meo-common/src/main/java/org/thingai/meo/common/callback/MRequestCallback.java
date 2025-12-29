package org.thingai.meo.common.callback;

public interface MRequestCallback<T> {
    void onSuccess(T result, String message);
    void onFailure(int errorCode, String errorMessage);
}
