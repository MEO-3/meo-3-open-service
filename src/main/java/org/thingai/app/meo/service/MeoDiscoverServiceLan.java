package org.thingai.app.meo.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.thingai.app.meo.handler.device.MDeviceConfigLanHandler;
import org.thingai.base.log.ILog;
import org.thingai.meo.common.define.MConnectionType;
import org.thingai.meo.common.entity.MDeviceConfigLan;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Listens for UDP broadcast discovery packets from MEO devices and
 * stores them in MDevDiscoverHandler.
 *
 * Expected JSON payload (aligned with ESP32 library):
 * {
 *   "magic": "MEO3_DISCOVERY_V1",
 *   "label": "DIY Sensor",
 *   "model": "ESP32-DevKit",
 *   "manufacturer": "MEO DIY",
 *   "connectionType": 0,
 *   "mac": "AA:BB:CC:DD:EE:FF",
 *   "ip": "192.168.1.101",
 *   "listen_port": 8091,
 *   "featureEvents": ["sensor_update"],
 *   "featureMethods": ["turn_on", "turn_off"]
 * }
 */
public class MeoDiscoverServiceLan implements Runnable {
    private static final String TAG = "MDiscoveryServiceLan";
    private static final String EXPECTED_MAGIC = "MEO3_DISCOVERY_V1";

    private final MDeviceConfigLanHandler discoverHandler;
    private final int port;
    private final Gson gson = new Gson();

    private volatile boolean running = true;

    public MeoDiscoverServiceLan(int port, MDeviceConfigLanHandler discoverHandler) {
        this.port = port;
        this.discoverHandler = discoverHandler;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        ILog.d(TAG, "Starting discovery listener on UDP port " + port);
        try (DatagramSocket socket = new DatagramSocket(port)) {
            socket.setBroadcast(true);

            while (running) {
                ILog.d(TAG, "Waiting for discovery packets...");
                try {
                    byte[] buffer = new byte[2048];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String payload = new String(packet.getData(), 0, packet.getLength());
                    ILog.d(TAG, "Received discovery packet from " + packet.getAddress().getHostAddress() + ": " + payload);

                    handleDiscoveryPayload(payload);
                } catch (IOException e) {
                    if (!running) {
                        break; // socket closed due to stop()
                    }
                    ILog.e(TAG, "Error receiving discovery packet: " + e.getMessage());
                }
            }
        } catch (SocketException e) {
            ILog.e(TAG, "Failed to open discovery socket: " + e.getMessage());
        }
        ILog.d(TAG, "Discovery listener stopped");
    }

    private void handleDiscoveryPayload(String payload) {
        try {
            DiscoveryPacket dp = gson.fromJson(payload, DiscoveryPacket.class);
            if (dp == null) {
                ILog.w(TAG, "Received null/invalid discovery JSON");
                return;
            }

            if (!EXPECTED_MAGIC.equals(dp.magic)) {
                ILog.d(TAG, "Discovery packet missing/invalid magic, ignoring");
                return;
            }

            MDeviceConfigLan info = new MDeviceConfigLan();
            info.setIpAddress(dp.ip);
            info.setMacAddress(dp.mac);
            info.setModel(dp.model);
            info.setPort(dp.listenPort);
            info.setManufacturer(dp.manufacturer);
            info.setConnectionType(dp.connectionType);
            info.setFeatureEvents(dp.featureEvents);
            info.setFeatureMethods(dp.featureMethods);

            boolean added = discoverHandler.addDeviceConfigLan(info);
            if (!added) {
                ILog.d(TAG, "Device with MAC " + dp.mac + " already discovered or list full");
                return;
            }

            ILog.i(TAG, "Discovered device: mac=" + dp.mac + ", ip=" + dp.ip);
        } catch (Exception e) {
            ILog.e(TAG, "Failed to handle discovery payload: " + e.getMessage());
        }
    }

    /**
     * Internal DTO matching the expected JSON from ESP32 devices.
     */
    private static class DiscoveryPacket {
        String magic;
        String label;
        String model;
        String manufacturer;
        int connectionType = MConnectionType.LAN;
        String mac;
        String ip;
        @SerializedName("listen_port")
        int listenPort;
        String[] featureEvents;
        String[] featureMethods;
    }
}