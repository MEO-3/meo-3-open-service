package org.thingai.app.meo.define;

public final class MeoCmd {
    private MeoCmd() {
    }

    public static final int CMD_GENERIC = 0x0001;
    public static final int CMD_WRITE = 0x0002;
    public static final int CMD_READ = 0x0003;
    public static final int CMD_EXECUTE = 0x0004;
    public static final int CMD_EXECUTE_WITH_VAR = 0x0005;
    public static final int CMD_STOP = 0x0006;

    public static final int EVENT_GENERIC = 0xE000;
    public static final int EVENT_BUTTON = 0xE001;

    public static final int READ_GENERIC = 0xF000;
    public static final int READ_TEMP = 0xF001;
    public static final int READ_HUMID = 0xF002;
    public static final int READ_PRESSURE = 0xF003;
    public static final int READ_CO2 = 0xF004;
    public static final int READ_PM25 = 0xF005;
    public static final int READ_DISTANCE = 0xF006;

    public static final int WRITE_GENERIC = 0xFF00;
    public static final int WRITE_LED = 0xFF01;
    public static final int WRITE_LED_RGB = 0xFF02;
    public static final int WRITE_BUZZER = 0xFF03;
    public static final int WRITE_MOTOR = 0xFF04;
    public static final int WRITE_SERVO = 0xFF05;
}
