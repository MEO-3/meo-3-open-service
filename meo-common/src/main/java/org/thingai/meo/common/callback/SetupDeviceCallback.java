package org.thingai.meo.common.callback;

import org.thingai.meo.common.entity.device.MDeviceInfo;

public interface SetupDeviceCallback {
    void onDeviceIdentifiedAndReady(MDeviceInfo deviceInfo);
    void onProgress(int progress, String statusMessage);
    void onSetupFailed(int errorCode, String errorMessage);
}
