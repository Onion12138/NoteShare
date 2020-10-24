package com.ecnu.note.utils;

import java.util.Random;

/**
 * @author onion
 * @date 2020/1/29 -3:24 下午
 */
public class KeyUtil {
    public static synchronized String getUniqueKey() {
        Random random = new Random();
        Integer number = random.nextInt(900000) + 100000;
        return System.currentTimeMillis() + String.valueOf(number);
    }
}
