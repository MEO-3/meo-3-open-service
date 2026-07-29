package org.thingai.app.meo.messaging;

import org.thingai.app.meo.util.ByteUtil;

/**
 * Event from meo/v1/device/{deviceId}/event: u16 cap | f32 value.
 * Parse-only. Carries READ readings or EVENT occurrences, never a WRITE result.
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
