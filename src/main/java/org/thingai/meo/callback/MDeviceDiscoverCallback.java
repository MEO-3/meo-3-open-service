package org.thingai.meo.callback;

import org.thingai.meo.entity.MDevice;
import org.thingai.meo.entity.MDeviceDiscoverInfo;

public interface MDeviceDiscoverCallback {
    void onDeviceDiscovered(MDeviceDiscoverInfo deviceInfo, String message);
    void onDeviceRegistered(MDevice device, String message);
    void onDeviceRegisteredFailed(int errorCode, String errorMessage);
}
