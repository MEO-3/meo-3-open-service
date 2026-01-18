package org.thingai.app.meo.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.thingai.app.meo.handler.device.MDeviceConfigLanHandler;
import org.thingai.base.log.ILog;
import org.thingai.meo.common.define.MConnectionType;
import org.thingai.meo.common.entity.device.MDeviceConfigLan;

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

    @Override
    public void run() {

    }
}