package com.ecnu.note.dao;

import com.ecnu.note.domain.mongo.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author onion
 * @date 2020/1/26 -4:05 下午
 */
public interface NoteDao extends MongoRepository<Note, String> {
    List<Note> findByAuthorEmail(String email);
    Page<Note> findAllByIdInAndValidIsTrue(List<String> list, Pageable pageable);
    List<Note> findAllByTag(String tag, Pageable pageable);
}
