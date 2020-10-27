package com.ecnu.note.domain.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author onion
 * @date 2020/1/29 -3:41 下午
 */
@Data
@Document(indexName = "note", shards = 1, replicas = 0)
public class NoteSearch {
    @Id
    private String id;
    @Field(type = FieldType.Keyword, index = false)
    private String email;
    @Field(type = FieldType.Keyword, index = false)
    private String createTime;
    @Field(type = FieldType.Keyword, index = false)
    private String updateTime;
    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String summary;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Keyword)
    private String tag;
}
