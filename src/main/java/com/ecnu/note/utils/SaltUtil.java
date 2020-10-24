package com.ecnu.note.utils;

import java.util.Random;

/**
 * @author onion
 * @date 2020/1/27 -6:07 下午
 */
public class SaltUtil {
    private static final String SOURCE = "0123456789";
    public static String getSalt() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 10; i++){
            sb.append(SOURCE.charAt(random.nextInt(10)));
        }
        return sb.toString();
    }
    public static String getCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 6; i++){
            sb.append(SOURCE.charAt(random.nextInt(10)));
        }
        return sb.toString();
    }
}
