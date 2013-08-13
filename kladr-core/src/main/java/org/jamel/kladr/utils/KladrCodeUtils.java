package org.jamel.kladr.utils;

/**
 * @author Sergey Polovko
 */
public final class KladrCodeUtils {

    private static final byte ZERO = (byte) '0';


    private KladrCodeUtils() {
    }

    public static boolean isValid(byte[] code) {
        int len = code.length;
        return code[len-1] == '0' && code[len-2] == '0';
    }

    public static byte getRegionId(byte[] code) {
        return (byte) ((code[0] - ZERO) * 10 + (code[1] - ZERO));
    }

    public static int getDistrictId(byte[] code) {
        return (code[2] - ZERO) * 100 + (code[3] - ZERO) * 10 + (code[4] - ZERO);
    }

    public static int getDistrictCode(byte[] code) {
        return NumberUtils.parseInt(code, 0, 5);
    }

    public static int getCityId(byte[] code) {
        return (code[5] - ZERO) * 100 + (code[6] - ZERO) * 10 + (code[7] - ZERO);
    }

    public static long getCityCode(byte[] code) {
        return NumberUtils.parseLong(code, 0, 8);
    }

    public static int getCountryId(byte[] code) {
        return (code[8] - ZERO) * 100 + (code[9] - ZERO) * 10 + (code[10] - ZERO);
    }

    public static long getCountryCode(byte[] code) {
        return NumberUtils.parseLong(code, 0, 11);
    }

    public static int getStreetId(byte[] code) {
        return (code[11] - ZERO) * 1000 + (code[12] - ZERO) * 100 + (code[13] - ZERO) * 10 + (code[14] - ZERO);
    }

    public static long getStreetCode(byte[] code) {
        return NumberUtils.parseLong(code, 0, 15);
    }

    public static boolean isCityRegion(byte regionCode) {
        return regionCode == 77 || regionCode == 78 || regionCode == 99;
    }
}
