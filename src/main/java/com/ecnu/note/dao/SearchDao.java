package com.ecnu.note.dao;

import com.ecnu.note.domain.search.NoteSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author onion
 * @date 2020/10/22 -10:37 上午
 */
public interface SearchDao extends ElasticsearchRepository<NoteSearch, String> {
}
