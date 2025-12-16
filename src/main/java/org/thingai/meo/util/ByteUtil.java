package org.thingai.meo.util;

public class ByteUtil {

    public static byte[] longToBytes(long value) {
        return new byte[] {
            (byte) (value >> 56),
            (byte) (value >> 48),
            (byte) (value >> 40),
            (byte) (value >> 32),
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }

    public static byte[] stringToBytes(String value) {
        return value != null ? value.getBytes() : new byte[0];
    }

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] hexStringToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];

        // convert uppercase to lowercase
        hex = hex.toLowerCase();
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                 + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] getCurrentTimestampBytes(int byteLength) {
        long currentTime = System.currentTimeMillis();

        byte[] fullBytes = longToBytes(currentTime);
        if (byteLength >= 8) {
            return fullBytes;
        }
        byte[] result = new byte[byteLength];

        // get bytes from the end
        System.arraycopy(fullBytes, 8 - byteLength, result, 0, byteLength);
        return result;
    }

    public static byte[] concatBytes(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

}
