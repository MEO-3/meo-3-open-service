package org.thingai.app.meo.handler.callback;

public interface RequestCallback<T> {
    void onResult(T response, String message);
    void onFailure(Throwable t, String message);
}
