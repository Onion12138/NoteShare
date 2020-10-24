package com.ecnu.note.domain.mongo;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
@Data
@Document(collection = "user")
@Builder
public class User {
    @Id
    private String email;
    private String username;
    private String password;
    private Boolean disabled;
    private String profileUrl;
    private LocalDate registerTime;
    private String salt;
}
