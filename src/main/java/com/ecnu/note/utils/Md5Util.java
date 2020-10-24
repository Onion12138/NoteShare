package com.ecnu.note.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author onion
 * @date 2020/1/28 -10:38 上午
 */
public class Md5Util {
    public static String encrypt(String source) {
        return encodeMd5(source.getBytes());
    }

    private static String encodeMd5(byte[] source) {
        try {
            return encodeHex(MessageDigest.getInstance("MD5").digest(source));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static String encodeHex(byte[] bytes) {
        StringBuilder buffer = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            if (((int) aByte & 0xff) < 0x10) {
                buffer.append("0");
            }
            buffer.append(Long.toString((int) aByte & 0xff, 16));
        }
        return buffer.toString();
    }
}
