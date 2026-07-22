package org.thingai.app.meo.messaging;

import org.thingai.app.meo.util.ByteUtil;

/**
 * Command frame sent to a device on meo/v1/device/{deviceId}/command.
 * Fixed 8-byte little-endian layout (docs/mqtt_messaging.md):
 * u16 requestId | u16 cap | i32 value
 *
 * Build-only: the gateway never receives a command, so there is no parse().
 */
public final class MeoCommandFrame {
    public static final int SIZE = 8;

    private final int requestId;
    private final int cap;
    private final int value;

    /**
     * @param requestId correlation id, uint16 (0..65535)
     * @param cap       capability from MeoCmd, uint16 (0..65535)
     * @param value     scalar for WRITE capabilities; ignored by the device otherwise
     */
    public MeoCommandFrame(int requestId, int cap, int value) {
        if (requestId < 0 || requestId > 0xFFFF) {
            throw new IllegalArgumentException("requestId out of uint16 range: " + requestId);
        }
        if (cap < 0 || cap > 0xFFFF) {
            throw new IllegalArgumentException("cap out of uint16 range: " + cap);
        }
        this.requestId = requestId;
        this.cap = cap;
        this.value = value;
    }

    public MeoCommandFrame(int requestId, int cap) {
        this(requestId, cap, 0);
    }

    public byte[] toBytes() {
        byte[] buf = new byte[SIZE];
        ByteUtil.putU16LE(buf, 0, requestId);
        ByteUtil.putU16LE(buf, 2, cap);
        ByteUtil.putI32LE(buf, 4, value);
        return buf;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getCap() {
        return cap;
    }

    public int getValue() {
        return value;
    }
}
