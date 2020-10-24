package com.ecnu.note.service.impl;

import com.ecnu.note.dao.SearchDao;
import com.ecnu.note.domain.search.NoteSearch;
import com.ecnu.note.service.SearchService;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * @author onion
 * @date 2020/10/22 -10:32 上午
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private SearchDao searchDao;

    @Override
    public Page<NoteSearch> findByKeyword(String keyword, int page) {
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("summary")
                .field("title");
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(keyword, "summary", "title")
                        .field("title", 3.0f)
                        .field("summary",0.4f)
                        .type(MultiMatchQueryBuilder.Type.MOST_FIELDS))
                .withHighlightBuilder(highlightBuilder)
                .withPageable(PageRequest.of(page - 1, 10 )).build();
        return searchDao.search(searchQuery);
    }

}
