package org.thingai.meo.handlers;

import org.thingai.base.dao.Dao;
import org.thingai.base.dao.DaoSqlite;
import org.thingai.meo.entities.MeoDevice;
import org.thingai.meo.entities.MeoDeviceInfo;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

public class MeoHandlerDevice {
    private final Dao<MeoDevice, Integer> deviceDao = new DaoSqlite<>(MeoDevice.class);

    private Thread scanThread;

    public synchronized void scanLanDevice(MeoDeviceScanCallback callback, long timeoutSeconds) {
        if (scanThread != null && scanThread.isAlive()) {
            callback.onError("A scan is already in progress. Please try again later.");
            return;
        }
        scanThread = new Thread(() -> {
            HashMap<String, MeoDeviceInfo> deviceMap = new HashMap<>();
            JmDNS jmdns = null;
            try {
                // Use local host for JmDNS (or bind to a specific interface if needed)
                InetAddress localHost = InetAddress.getLocalHost();
                jmdns = JmDNS.create(localHost);

                final Object lock = new Object();

                JmDNS finalJmdns = jmdns;
                ServiceListener listener = new ServiceListener() {
                    @Override
                    public void serviceAdded(ServiceEvent event) {
                        // Request service details
                        finalJmdns.requestServiceInfo(event.getType(), event.getName(), 1);
                    }

                    @Override
                    public void serviceRemoved(ServiceEvent event) {
                        // Optionally handle device removal
                    }

                    @Override
                    public void serviceResolved(ServiceEvent event) {
                        ServiceInfo info = event.getInfo();
                        MeoDeviceInfo deviceInfo = new MeoDeviceInfo();
                        deviceInfo.setTypeConnect(org.thingai.meo.defines.MeoTypeConnect.LAN);
                        deviceInfo.setIpAddress(info.getHostAddresses()[0]);
                        deviceInfo.setModel(info.getPropertyString("model"));
                        deviceInfo.setManufacturer(info.getPropertyString("manufacturer"));
                        deviceInfo.setFirmwareVersion(info.getPropertyString("fw"));
                        deviceInfo.setMacAddress(info.getPropertyString("mac").replace(":", "").toUpperCase());

                        // Avoid duplicates based on MAC address
                        if (!deviceMap.containsKey(deviceInfo.getMacAddress())) {
                            deviceMap.put(deviceInfo.getMacAddress(), deviceInfo);
                            callback.onDeviceFound(deviceInfo, "Device found: " + deviceInfo.getMacAddress());
                        }
                    }
                };

                String serviceType = "_meo._tcp.local.";
                jmdns.addServiceListener(serviceType, listener);

                // Wait for timeoutSeconds then finish
                synchronized (lock) {
                    lock.wait(timeoutSeconds * 1000L);
                }

            } catch (Exception e) {
                callback.onError("Error during LAN scan: " + e.getMessage());
            } finally {
                if (jmdns != null) {
                    try { jmdns.close(); } catch (Exception e) { /* ignore */ }
                }
            }
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
    }

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
