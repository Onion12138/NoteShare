package com.ecnu.note.domain.mongo;

import com.ecnu.note.vo.UserVO;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author onion
 * @date 2020/1/27 -8:30 上午
 */
@Data
@Builder
@Document(collection = "note")
public class Note implements Serializable {
    @Id
    private String id;
    private UserVO author;
    private String title;
    private Boolean authority;
    private String forkFrom;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String tag;
    private String summary;
    private String content;
    private Boolean valid;
    private Integer star;
    private Integer hate;
    private Integer view;
    private Integer collect;
    private Integer fork;
}
