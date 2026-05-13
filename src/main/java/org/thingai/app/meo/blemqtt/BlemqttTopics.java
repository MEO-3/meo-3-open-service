package org.thingai.app.meo.blemqtt;

public final class BlemqttTopics {
    public static final String COMMAND = "blemqtt/v1/command";
    public static final String EVENT = "blemqtt/v1/event";
    public static final String STATUS = "blemqtt/v1/status";
    public static final String REPLY_PREFIX = "blemqtt/v1/reply/";
    public static final String REPLY_WILDCARD = REPLY_PREFIX + "+";

    private BlemqttTopics() {
    }

    public static String reply(String requestId) {
        return REPLY_PREFIX + requestId;
    }
}
