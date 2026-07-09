package org.thingai.app.meo.callback;

public interface RequestCallback<T> {
    void onResult(T response, String message);
    void onFailure(int errorCode, String message);
}
