package org.thingai.meo.callback;

import org.thingai.meo.entity.MDevice;

public interface MDeviceDiscoverCallback {
    void onDeviceRegistered(MDevice device, String message);
    void onRegisteredFailed(int errorCode, String errorMessage);
}
