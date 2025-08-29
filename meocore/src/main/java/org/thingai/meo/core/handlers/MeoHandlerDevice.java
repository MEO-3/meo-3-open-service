package org.thingai.meo.core.handlers;

import org.thingai.meo.core.entities.MeoDevice;
import org.thingai.meo.core.entities.MeoDeviceInfo;

import java.util.List;

public abstract class MeoHandlerDevice {
    public abstract void scanLanDevice(MeoDeviceScanCallback callback, long timeoutSeconds);

    public abstract void checkBleDongleStatus(MeoDeviceBleDongleStatusCallback callback, int timeoutSeconds);

    public abstract void scanBleDevice(int dongleEid, MeoDeviceScanCallback callback, int timeoutSeconds);

    public abstract void stopScanDevice();

    public abstract void configureDevice(MeoDeviceInfo deviceInfo, MeoDeviceConfigureCallback callback);

    public interface MeoDeviceScanCallback {
        void onDeviceFound(MeoDeviceInfo deviceInfo, String message);
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
