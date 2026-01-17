package org.thingai.meo.common.handler;

import org.thingai.meo.common.callback.RequestCallback;
import org.thingai.meo.common.entity.device.MDeviceConfigBle;
import org.thingai.meo.common.entity.info.MWifiInfo;

public abstract class MDeviceDiscoveryBleHandler implements IDeviceConfigHandler<MDeviceConfigBle> {
    public static final String TAG = "MDeviceDiscoveryBleHandler";

    public abstract void scanWifi(RequestCallback<MWifiInfo[]> callback);
    public abstract void connectWifi(String ssid, String password, RequestCallback<Boolean> callback);
}
