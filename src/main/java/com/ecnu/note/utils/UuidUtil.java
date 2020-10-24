package com.ecnu.note.utils;

import java.util.UUID;

/**
 * @author onion
 * @date 2020/1/27 -6:24 下午
 */
public class UuidUtil {
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
