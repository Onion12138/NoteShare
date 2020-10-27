package com.ecnu.note.service;

import java.util.Map;

/**
 * @author onion
 * @date 2020/10/22 -10:32 上午
 */
public interface SearchService {
    Map<String, Object> findByKeyword(String keyword, int page);
}
