package com.ecnu.note.service;

import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.domain.mongo.Note;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author onion
 * @date 2020/1/27 -8:35 上午
 */
public interface NoteService {

    String publishNote(Map<String, String> map);

    void updateNote(Map<String, String> map);

    void deleteNote(String noteId);

    Note findOneNote(String email, String noteId);

    void changeAuthority(String noteId, String authority);

    void starOrHate(String type, String noteId, String email);

    String uploadPicture(String noteId, MultipartFile file);

    List<Note> findByAuthor(String email);

    Set<String> findHotTag();

    List<Knowledge> searchMindMap(String keyword);

    Page<Note> findMyNote(String email, Integer page, Integer size);
}
