package org.thingai.meo.common.handler;

import org.thingai.meo.common.callback.RequestCallback;
import org.thingai.meo.common.callback.SetupDeviceCallback;
import org.thingai.meo.common.entity.device.MDevice;
import org.thingai.meo.common.entity.device.MDeviceConfig;

public interface IDeviceConfigHandler<T extends MDeviceConfig> {
    boolean discovery();
    boolean closeDiscovery();
    void onDeviceFound(RequestCallback<T> callback);
    void connectAndIdentifyDevice(T deviceConfig, SetupDeviceCallback callback);
    void setupAndSyncDeviceLocal(String label, RequestCallback<MDevice> callback);
    void setupAndSyncDeviceToCloud(String label, RequestCallback<MDevice> callback);
}
