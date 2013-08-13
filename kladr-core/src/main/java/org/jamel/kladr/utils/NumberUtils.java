package org.jamel.kladr.utils;

/**
 * @author Sergey Polovko
 */
public class NumberUtils {

    public static long parseLong(byte[] bytes, int from, int to) {
        long result = 0;
        for (int i = from; i < to; i++) {
            result *= 10;
            result += (bytes[i] - (byte) '0');
        }
        return result;
    }

    public static int parseInt(byte[] bytes, int from, int to) {
        int result = 0;
        for (int i = from; i < to; i++) {
            result *= 10;
            result += (bytes[i] - (byte) '0');
        }
        return result;
    }
}
