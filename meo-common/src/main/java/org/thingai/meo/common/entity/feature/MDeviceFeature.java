package org.thingai.meo.common.entity.feature;

public abstract class MDeviceFeature {
    protected String deviceId;

    public abstract String getDeviceId();
    public abstract String getFeatureName();
}
