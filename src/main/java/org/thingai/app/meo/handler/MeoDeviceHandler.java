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
        if (deviceId == null || deviceId.trim().isEmpty()) {
            return null;
        }
        MeoDevice[] devices = dao.query(MeoDevice.class, "deviceId", deviceId);
        return devices != null && devices.length > 0 ? devices[0] : null;
    }

    // TODO: MeoDevice[] getDevices
    // TODO: MeoDevice updateDevice(String deviceId, MeoDevice device);
    // TODO: MeoDevice deleteDevice(String deviceId)
}
