package org.thingai.meo.callbacks;

import org.thingai.meo.entities.MeoDevice;

public interface MeoDeviceConfigCallback {
    void onDeviceConfigUpdated(MeoDevice device);
    void onDeviceConfigError(String error);
}
