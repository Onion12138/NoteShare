package com.ecnu.note.service;

import com.ecnu.note.domain.search.NoteSearch;
import org.springframework.data.domain.Page;

/**
 * @author onion
 * @date 2020/10/22 -10:32 上午
 */
public interface SearchService {
    Page<NoteSearch> findByKeyword(String keyword, int page);
}
