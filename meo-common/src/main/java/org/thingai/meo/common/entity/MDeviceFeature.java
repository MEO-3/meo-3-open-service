package org.thingai.meo.common.entity;

public abstract class MDeviceFeature {
    protected String deviceId;

    public abstract String getDeviceId();
    public abstract String getFeatureName();
}
