package com.example.url_shortener.util;

public class Base62Encoder {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = BASE62.length();

    public static String encode (long value) {
        StringBuilder sb = new StringBuilder();
        while ( value > 0 ) {
            sb.append(BASE62.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return sb.reverse().toString();
    }

    public static long decode(String shortCode) {
        long result = 0;
        for (char c : shortCode.toCharArray()) {
            result = result * BASE + BASE62.indexOf(c);
        }
        return result;
    }
}
