package org.thingai.meo.common.handler;

import org.thingai.meo.common.entity.config.MDeviceConfig;

import java.util.LinkedList;

public abstract class MDeviceConfigHandler<T extends MDeviceConfig> {
    protected LinkedList<T> bufferDeviceConfig = new LinkedList<>();

    public abstract void configAndSyncDevice(T deviceConfig);
}
