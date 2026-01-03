package org.thingai.meo.common.handler;

import org.thingai.meo.common.callback.MDeviceConfigCallback;
import org.thingai.meo.common.entity.config.MDeviceConfig;

public interface MDeviceConfigHandler<T extends MDeviceConfig> {
    void configAndSyncDevice(T deviceConfig, MDeviceConfigCallback callback);
}
