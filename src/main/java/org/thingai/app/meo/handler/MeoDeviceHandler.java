package org.thingai.app.meo.handler;

import org.thingai.app.meo.entity.MeoDevice;
import org.thingai.app.meo.entity.MeoDeviceCapability;
import org.thingai.base.log.ILog;
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

    public MeoDevice[] getDevices() {
        MeoDevice[] devices = dao.readAll(MeoDevice.class);
        return devices != null ? devices : new MeoDevice[0];
    }

    // Only user metadata is updatable; identity (deviceId, macAddress) and
    // firmware-reported fields (model, fwVersion, transportType) are owned by
    // the provisioning flow.
    public MeoDevice updateDevice(String deviceId, MeoDevice update) {
        MeoDevice existing = getDevice(deviceId);
        if (existing == null || update == null) {
            return null;
        }
        existing.setName(update.getName());
        existing.setDescription(update.getDescription());
        existing.setDeviceType(update.getDeviceType());
        dao.insertOrUpdate(existing);
        ILog.i(TAG, "updateDevice", "updated deviceId=" + deviceId);
        return existing;
    }

    public MeoDevice deleteDevice(String deviceId) {
        MeoDevice existing = getDevice(deviceId);
        if (existing == null) {
            return null;
        }
        dao.delete(existing);
        dao.deleteByColumn(MeoDeviceCapability.class, "deviceId", deviceId);
        ILog.i(TAG, "deleteDevice", "deleted deviceId=" + deviceId);
        return existing;
    }

    public int[] getCapabilities(String deviceId) {
        MeoDeviceCapability[] rows = dao.query(MeoDeviceCapability.class, "deviceId", deviceId);
        if (rows == null) {
            return new int[0];
        }
        int[] capabilities = new int[rows.length];
        for (int i = 0; i < rows.length; i++) {
            capabilities[i] = rows[i].getCapabilityId();
        }
        return capabilities;
    }
}
