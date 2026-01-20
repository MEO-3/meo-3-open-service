package org.thingai.meo.common.handler;

import org.thingai.meo.common.callback.SetupDeviceCallback;
import org.thingai.meo.common.entity.device.MDeviceConfig;

public abstract class MDeviceDiscoveryHandler<T extends MDeviceConfig> implements IDeviceConfigHandler<T> {
    protected SetupDeviceCallback<T> setupDeviceCallback;

    public void setSetupDeviceCallback(SetupDeviceCallback<T> callback) {
        this.setupDeviceCallback = callback;
    }
}
