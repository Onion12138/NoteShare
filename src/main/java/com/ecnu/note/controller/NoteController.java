package com.ecnu.note.controller;

import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.domain.mongo.Note;
import com.ecnu.note.service.NoteService;
import com.ecnu.note.utils.AuthUtil;
import com.ecnu.note.vo.BaseResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author onion
 * @date 2020/1/27 -8:34 上午
 */
@RestController
@Slf4j
@RequestMapping("/note")
public class NoteController {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private NoteService noteService;


    @PostMapping("/starOrHate")
    public BaseResponseVO starOrHate(@RequestParam String type, @RequestParam String noteId, @RequestParam String description) {
        String email = AuthUtil.getEmail();
        noteService.starOrHate(type, noteId, email);
        return BaseResponseVO.success();
    }

    @PostMapping("/publish")
    public BaseResponseVO publishNote(@RequestParam Map<String, String> map) {
        String email = AuthUtil.getEmail();
        map.put("authorEmail", email);
        String id = noteService.publishNote(map);
        return BaseResponseVO.success(id);
    }

    @PostMapping("/update")
    public BaseResponseVO updateNote(@RequestParam Map<String, String> map) {
        noteService.updateNote(map);
        return BaseResponseVO.success();
    }

    @PostMapping("/changeAuthority")
    public BaseResponseVO changeAuthority(@RequestParam String noteId, @RequestParam String authority) {
        noteService.changeAuthority(noteId, authority);
        return BaseResponseVO.success();
    }

    @PostMapping("/delete")
    public BaseResponseVO deleteNote(@RequestParam String noteId) {
        noteService.deleteNote(noteId);
        return BaseResponseVO.success();
    }

    @GetMapping("/findOne")
    public BaseResponseVO findNote(@RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        Note note = noteService.findOneNote(email, noteId);
        return BaseResponseVO.success(note);
    }

    @PostMapping("/uploadPicture")
    public BaseResponseVO uploadPicture(@RequestParam String noteId, @RequestParam MultipartFile file) {
        String uri = noteService.uploadPicture(noteId, file);
        return BaseResponseVO.success(uri);
    }

    @GetMapping("/hotTag")
    public BaseResponseVO findHotTag() {
        Set<String> tags = noteService.findHotTag();
        return BaseResponseVO.success(tags);
    }

    @GetMapping("/myPublish")
    public BaseResponseVO findMyPublish() {
        String email = AuthUtil.getEmail();
        List<Note> notes = noteService.findByAuthor(email);
        return BaseResponseVO.success(notes);
    }

    @GetMapping("/searchMindMap")
    public BaseResponseVO searchMindMap(@RequestParam String keyword) {
        List<Knowledge> mindMap = noteService.searchMindMap(keyword);
        return BaseResponseVO.success(mindMap);
    }

    @GetMapping("/findMyNote")
    public BaseResponseVO findMyPublish(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        String email = AuthUtil.getEmail();
        Page<Note> notes = noteService.findMyNote(email, page - 1, size);
        return BaseResponseVO.success(notes);
    }

}
