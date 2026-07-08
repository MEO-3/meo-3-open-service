package org.thingai.app.meo.handler;

import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.base.dao.Dao;

public class MeoDeviceHandler {
    private static final String TAG = "MeoDeviceHandler";

    private final Dao dao;

    public MeoDeviceHandler(Dao dao) {
        this.dao = dao;
    }

    public MeoDevice getDevice(String deviceId) {
        return null;
    }
}
