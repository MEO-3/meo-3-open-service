package org.thingai.meo.common.callback;

import org.thingai.meo.common.entity.device.MDevice;

public interface MDeviceConfigCallback {
    void onConfigAndSyncSuccess(MDevice device, String message);
    void onConfigAndSyncFailure(int errorCode, String errorMessage);

}
