package org.thingai.app.meo.messaging;

import org.thingai.app.meo.util.ByteUtil;

/**
 * Reply frame received from a device on meo/v1/device/{deviceId}/reply.
 * Fixed 10-byte little-endian layout (docs/mqtt_messaging.md):
 * u16 requestId | u8 ok | u16 cap | 4-byte value | u8 error
 *
 * Parse-only: the gateway never builds a reply.
 *
 * The value bytes are i32 for WRITE capabilities and f32 for READ ones. The
 * frame keeps them raw and offers asInt()/asFloat(); the caller knows which
 * capability it sent, so it knows which accessor applies.
 */
public final class MeoReplyFrame {
    public static final int SIZE = 10;

    private final int requestId;
    private final boolean ok;
    private final int cap;
    private final int rawValue;
    private final int error;

    private MeoReplyFrame(int requestId, boolean ok, int cap, int rawValue, int error) {
        this.requestId = requestId;
        this.ok = ok;
        this.cap = cap;
        this.rawValue = rawValue;
        this.error = error;
    }

    public static MeoReplyFrame parse(byte[] payload) {
        if (payload == null || payload.length != SIZE) {
            throw new IllegalArgumentException(
                    "reply frame must be " + SIZE + " bytes, got "
                            + (payload == null ? "null" : String.valueOf(payload.length)));
        }
        return new MeoReplyFrame(
                ByteUtil.getU16LE(payload, 0),
                payload[2] != 0,
                ByteUtil.getU16LE(payload, 3),
                ByteUtil.getI32LE(payload, 5),
                payload[9] & 0xFF);
    }

    /** Value as written, for WRITE capabilities. */
    public int asInt() {
        return rawValue;
    }

    /** Value as read, for READ capabilities. */
    public float asFloat() {
        return Float.intBitsToFloat(rawValue);
    }

    public int getRequestId() {
        return requestId;
    }

    public boolean isOk() {
        return ok;
    }

    public int getCap() {
        return cap;
    }

    /** MeoCmdErrCode value; 0 when ok. */
    public int getError() {
        return error;
    }
}
