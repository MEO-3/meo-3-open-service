package org.thingai.app.meo.messaging;

import org.thingai.app.meo.util.ByteUtil;

/**
 * Event frame received from a device on meo/v1/device/{deviceId}/event:
 * periodic readings and edge-triggered occurrences.
 * Fixed 6-byte little-endian layout (docs/mqtt_messaging.md):
 * u16 cap | f32 value
 *
 * Parse-only: the gateway never builds an event. The value is always f32 —
 * events carry READ readings or EVENT occurrences, never a WRITE result.
 */
public final class MeoEventFrame {
    public static final int SIZE = 6;

    private final int cap;
    private final float value;

    private MeoEventFrame(int cap, float value) {
        this.cap = cap;
        this.value = value;
    }

    public static MeoEventFrame parse(byte[] payload) {
        if (payload == null || payload.length != SIZE) {
            throw new IllegalArgumentException(
                    "event frame must be " + SIZE + " bytes, got "
                            + (payload == null ? "null" : String.valueOf(payload.length)));
        }
        return new MeoEventFrame(
                ByteUtil.getU16LE(payload, 0),
                ByteUtil.getF32LE(payload, 2));
    }

    public int getCap() {
        return cap;
    }

    public float getValue() {
        return value;
    }
}
