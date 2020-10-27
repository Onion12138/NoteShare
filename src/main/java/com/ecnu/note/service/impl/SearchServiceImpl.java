package com.ecnu.note.service.impl;

import com.alibaba.fastjson.JSON;
import com.ecnu.note.domain.search.NoteSearch;
import com.ecnu.note.service.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author onion
 * @date 2020/10/22 -10:32 上午
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    public Map<String, Object> findByKeyword(String keyword, int page) {

        SearchRequest searchRequest = new SearchRequest("note");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.from(page);
        sourceBuilder.size(10);

        MultiMatchQueryBuilder builder = new MultiMatchQueryBuilder(keyword,"title", "summary");
        sourceBuilder.query(builder);

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.field("summary");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<NoteSearch> list = new ArrayList<>();
        assert searchResponse != null;
        for (SearchHit documentFields : searchResponse.getHits()){
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            HighlightField summary = highlightFields.get("summary");
            NoteSearch noteSearch = JSON.parseObject(documentFields.getSourceAsString(), NoteSearch.class);
            if(title != null){
                Text[] fragments = title.fragments();
                StringBuilder sb = new StringBuilder();
                for(Text text : fragments){
                    sb.append(text);
                }
                noteSearch.setTitle(sb.toString());
            }
            if(summary != null){
                Text[] fragments = summary.fragments();
                StringBuilder sb = new StringBuilder();
                for(Text text : fragments){
                    sb.append(text);
                }
                noteSearch.setSummary(sb.toString());
            }
            list.add(noteSearch);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("search", list);
        map.put("total", searchResponse.getHits().totalHits);
        return map;
    }
}
