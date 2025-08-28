package org.thingai.meo.handlers;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.meo.defines.MeoTypeConnect;
import org.thingai.meo.entities.MeoDevice;
import org.thingai.meo.entities.MeoDeviceInfo;

import java.util.List;

public class MeoHandlerDevice {
    private final Dao<MeoDevice, Integer> deviceDao = new DaoSqlite<>(MeoDevice.class);

    private Thread scanThread;

    public void scanLanDevice(MeoDeviceScanCallback callback, int timeoutSeconds) {
        if (scanThread != null && scanThread.isAlive()) {
            callback.onError("A scan is already in progress.");
            return;
        }
        scanThread = new Thread(() -> {
            // TODO("Implement LAN device scanning logic");
        });
        scanThread.start();
    }

    public void checkBleDongleStatus(MeoDeviceBleDongleStatusCallback callback, int timeoutSeconds) {
    }

    public void scanBleDevice(int dongleEid, MeoDeviceScanCallback callback, int timeoutSeconds) {
        if (scanThread != null && scanThread.isAlive()) {
            callback.onError("A scan is already in progress.");
            return;
        }
        scanThread = new Thread(() -> {
            // TODO("Implement BLE device scanning logic");
        });
        scanThread.start();
    }

    public void stopScanDevice() {
        if (scanThread != null && scanThread.isAlive()) {
            scanThread.interrupt();
        }
    }

    public void configureDevice(MeoDeviceInfo deviceInfo, MeoDeviceConfigureCallback callback) {
        if (deviceInfo == null) {
            callback.onError("Device info is null.");
            return;
        }

        if (deviceInfo.getTypeConnect() == MeoTypeConnect.LAN) { // LAN device configuration
            // TODO("Implement LAN device configuration");
        } else if (deviceInfo.getTypeConnect() == MeoTypeConnect.BLE) { // BLE device configuration
            // TODO("Implement BLE device configuration");
        } else {
            callback.onError("Unknown device connection type.");
        }
    }

    public interface MeoDeviceScanCallback {
        void onDevicesFound(List<MeoDeviceInfo> deviceInfos, boolean isDevicesFound, String message);
        void onError(String message);
    }

    public interface MeoDeviceBleDongleStatusCallback {
        void onStatusChecked(boolean isDongleAvailable, List<Integer> dongleEids, String message);
        void onError(String message);
    }

    public interface MeoDeviceConfigureCallback {
        void onConfigured(MeoDevice meoDevice, String message);
        void onError(String message);
    }
}
