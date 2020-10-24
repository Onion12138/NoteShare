package com.ecnu.note.domain.search;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;

/**
 * @author onion
 * @date 2020/1/29 -3:41 下午
 */
@Data
@Builder
@Document(indexName = "Note")
public class NoteSearch {
    private String id;
    private String email;
    private String createTime;
    private String updateTime;
    private String summary;
    private String title;
    private String tag;
}
