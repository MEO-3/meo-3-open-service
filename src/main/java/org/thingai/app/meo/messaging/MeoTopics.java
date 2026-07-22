package org.thingai.app.meo.messaging;

/**
 * Device messaging topics (docs/mqtt_messaging.md). Not BlemqttTopics, which is
 * the internal channel to the Rust BLE service.
 */
public final class MeoTopics {
    public static final String PREFIX = "meo/v1/device/";
    public static final String REPLY_WILDCARD = PREFIX + "+/reply";
    public static final String EVENT_WILDCARD = PREFIX + "+/event";

    private MeoTopics() {
    }

    public static String command(String deviceId) {
        return PREFIX + deviceId + "/command";
    }

    /** deviceId from a device topic; null when the topic does not match. */
    public static String deviceId(String topic) {
        if (topic == null || !topic.startsWith(PREFIX)) {
            return null;
        }
        int end = topic.indexOf('/', PREFIX.length());
        return end > PREFIX.length() ? topic.substring(PREFIX.length(), end) : null;
    }
}
