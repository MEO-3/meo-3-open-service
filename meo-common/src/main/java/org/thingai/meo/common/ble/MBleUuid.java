package org.thingai.meo.common.ble;

import java.util.Locale;
import java.util.UUID;

public class MBleUuid {
    private static UUID u16(int value) {
        return UUID.fromString(String.format(Locale.US, "9F27%04X-0000-1000-8000-00805F9B34FB", value));
    }

    public static final UUID MEO_BLE_PROV_SERV_UUID = u16(0xF7F0);
    public static final UUID CH_UUID_WIFI_SSID = u16(0xF7F1);
    public static final UUID CH_UUID_WIFI_PASS = u16(0xF7F2);
    public static final UUID CH_UUID_WIFI_LIST = u16(0xF7F3);
    public static final UUID CH_UUID_USER_ID = u16(0xF7F4);
    public static final UUID CH_UUID_PRODUCT_ID = u16(0xF7F5);
    public static final UUID CH_UUID_BUILD_INFO = u16(0xF7F6);
    public static final UUID CH_UUID_MAC_ADDR = u16(0xF7F7);
    public static final UUID CH_UUID_DEV_MODEL = u16(0xF7F8);
    public static final UUID CH_UUID_DEV_MANUF = u16(0xF7F9);
    public static final UUID CH_UUID_TX_KEY = u16(0xF7FA);
}
