package org.thingai.meo.handler;

import org.thingai.base.log.ILog;
import org.thingai.meo.callback.MRequestCallback;
import org.thingai.meo.entity.MDevice;
import org.thingai.meo.entity.MDeviceDiscoverInfo;
import org.thingai.meo.util.ByteUtil;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

public class MDevDiscoverHandler {
    private static final String TAG = "MDevDiscoverHandler";

    private final LinkedList<MDeviceDiscoverInfo> deviceDiscoverInfos;
    private final int maxSize;

    private MDevMgmtHandler deviceManager;

    public MDevDiscoverHandler(int maxSize, MDevMgmtHandler deviceManager) {
        this.deviceManager = deviceManager;
        this.maxSize = maxSize;
        this.deviceDiscoverInfos = new LinkedList<>();
    }

    public synchronized boolean addDiscoveredDeviceInfo(MDeviceDiscoverInfo deviceInfo) {
        if (deviceDiscoverInfos.size() >= maxSize) {
            return false;
        }
        for (MDeviceDiscoverInfo info : deviceDiscoverInfos) {
            if (info.getMacAddress().equals(deviceInfo.getMacAddress())) {
                return true; // Device already discovered
            }
        }
        deviceDiscoverInfos.add(deviceInfo);
        return true;
    }

    public synchronized MDeviceDiscoverInfo[] getDiscoveredDeviceInfo() {
        return deviceDiscoverInfos.toArray(new MDeviceDiscoverInfo[0]);
    }

    public synchronized void registerDevice(int index, String label, MRequestCallback<MDevice> callback) {
        MDeviceDiscoverInfo deviceInfo = deviceDiscoverInfos.get(index);
        if (deviceInfo == null) {
            callback.onFailure(-1, "Invalid device index");
            return;
        }

        // Init MDevice object
        MDevice device = new MDevice();
        device.setId(generateDeviceId(deviceInfo));
        device.setLabel(label);
        device.setConnectionType(deviceInfo.getConnectionType());
        device.setManufacturer(deviceInfo.getManufacturer());
        device.setModel(deviceInfo.getModel());

        String transmitKey = generateTransmitKey(device);

        // Send transmit key back to device via tcp on port 8091 (device tcp server listening port)
        try {
            sendResponseToDevice(deviceInfo, device.getId(), transmitKey);
            deviceManager.addDevice(device);
            callback.onSuccess(device, "Device registered successfully");
        } catch (Exception e) {
            callback.onFailure(-1, "Failed to send response to device: " + e.getMessage());
        }
    }

    public synchronized void clearDiscoverList() {
        deviceDiscoverInfos.clear();
    }

    private String generateDeviceId(MDeviceDiscoverInfo deviceInfo) {
        // 4 bytes unix timestamp + 6 bytes MAC address
        long timestamp = System.currentTimeMillis() / 1000L;
        byte[] macBytes = ByteUtil.stringToBytes(deviceInfo.getMacAddress());
        byte[] timestampBytes = ByteUtil.longToBytes(timestamp);

        byte[] deviceIdBytes = new byte[10];
        System.arraycopy(timestampBytes, 0, deviceIdBytes, 0, 4);
        System.arraycopy(macBytes, 0, deviceIdBytes, 4, 6);
        return ByteUtil.bytesToHexString(deviceIdBytes);
    }

    private String generateTransmitKey(MDevice device) {
        // Transmit key is derived from device ID and label
        String source = device.getId() + device.getLabel();
        byte[] sourceBytes = ByteUtil.stringToBytes(source);
        // Simple hash function (for illustration purposes)
        int hash = 7;
        for (byte b : sourceBytes) {
            hash = hash * 31 + b;
        }
        return Integer.toHexString(hash);
    }

    private void sendResponseToDevice(MDeviceDiscoverInfo deviceInfo, String deviceId, String transmitKey) {
        String hostName = deviceInfo.getIpAddress();
        int port = deviceInfo.getListeningPort();

        try (Socket socket = new Socket(hostName, port)) {
            String responsePayload = String.format("{\"device_id\":\"%s\",\"transmit_key\":\"%s\"}", deviceId, transmitKey);

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(responsePayload);
            socket.close();

            ILog.d(TAG, "Sent response to device at " + hostName + ":" + port);
            ILog.d(TAG, "Response payload: " + responsePayload);
        } catch (Exception e) {
            // Handle exception (e.g., log error)
            e.printStackTrace();
            ILog.e(TAG, "Failed to send response to device: " + e.getMessage());
            throw new RuntimeException("Failed to send response to device", e);
        }

    }
}
