package org.thingai.app.meo.callback;

public interface ProvisionEventListener {
    void onEvent(String event, Object payload);
}
