package org.jamel.kladr.utils;

/**
 * @author Sergey Polovko
 */
public class Check {

    public static void fail(String message) {
        throw new IllegalStateException(message);
    }

    public static void isNull(Object o) {
        if (o != null) fail("must be null");
    }

    public static void isNull(Object o, String message) {
        if (o != null) fail(message);
    }

}
