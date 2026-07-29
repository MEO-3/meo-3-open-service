package org.thingai.app.meo.messaging;

import org.thingai.app.meo.util.ByteUtil;

/**
 * Reply from meo/v1/device/{deviceId}/reply: u16 requestId | u8 ok | u16 cap | 4B value | u8 error.
 * Parse-only. value is i32 for WRITE, f32 for READ — caller picks asInt()/asFloat().
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
