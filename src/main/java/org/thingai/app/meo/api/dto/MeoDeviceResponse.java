package org.thingai.app.meo.api.dto;

import org.thingai.app.meo.entity.MeoDevice;

// API read model: a device row joined with its capability ids. Capabilities are
// stored one-per-row in meo_device_capabilities; this flattens them to an int[]
// for the client. Not persisted.
public class MeoDeviceResponse {
    private String deviceId;
    private String name;
    private String description;
    private String macAddress;
    private int deviceType;
    private int transportType;
    private String model;
    private String fwVersion;
    private int[] capabilities;

    public static MeoDeviceResponse of(MeoDevice device, int[] capabilities) {
        MeoDeviceResponse view = new MeoDeviceResponse();
        view.deviceId = device.getDeviceId();
        view.name = device.getName();
        view.description = device.getDescription();
        view.macAddress = device.getMacAddress();
        view.deviceType = device.getDeviceType();
        view.transportType = device.getTransportType();
        view.model = device.getModel();
        view.fwVersion = device.getFwVersion();
        view.capabilities = capabilities != null ? capabilities : new int[0];
        return view;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public int getTransportType() {
        return transportType;
    }

    public String getModel() {
        return model;
    }

    public String getFwVersion() {
        return fwVersion;
    }

    public int[] getCapabilities() {
        return capabilities;
    }
}
