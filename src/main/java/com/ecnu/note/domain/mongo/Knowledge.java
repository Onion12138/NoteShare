package com.ecnu.note.domain.mongo;

import com.ecnu.note.domain.MindMap;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * @author onion
 * @date 2020/10/24 -10:26 上午
 */
@Data
@Builder
@Document(collection = "knowledge")
public class Knowledge {
    @Id
    private String id;
    private String email;
    private String title;
    private MindMap mindMap;
    private Boolean share;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
