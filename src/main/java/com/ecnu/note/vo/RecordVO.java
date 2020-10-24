package com.ecnu.note.vo;

import com.ecnu.note.domain.mongo.Note;
import lombok.Data;

import java.util.Date;

/**
 * @author onion
 * @date 2020/10/24 -7:57 下午
 */
@Data
public class RecordVO {
    private String id;
    private Date time;
    private Note note;
}
