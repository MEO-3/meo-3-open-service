package org.thingai.meo.entity;

public abstract class MDeviceFeature {
    protected String deviceId;
    protected String featureName;

    public abstract String getDeviceId();
    public abstract String getFeatureName();
}
