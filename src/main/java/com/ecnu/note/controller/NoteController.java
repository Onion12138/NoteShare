package com.ecnu.note.controller;

import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.domain.mongo.Note;
import com.ecnu.note.service.NoteService;
import com.ecnu.note.utils.AuthUtil;
import com.ecnu.note.vo.BaseResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;
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
    private NoteService noteService;
    /**
    * @description: 点赞/踩
    * @param:  type取值star或hate
    */
    @PostMapping("/starOrHate")
    public BaseResponseVO starOrHate(@RequestParam String type, @RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        noteService.starOrHate(type, noteId, email);
        return BaseResponseVO.success();
    }

    /**
    * @description: 发布笔记
    * @param:  map字段 authorEmail authority content tag title forkFrom(原创则为空串)
    */
    @PostMapping("/publish")
    public BaseResponseVO publishNote(@RequestParam Map<String, String> map) {
        String email = AuthUtil.getEmail();
        map.put("authorEmail", email);
        String id = noteService.publishNote(map);
        return BaseResponseVO.success(id);
    }

    /**
    * @description: 更新笔记
    * @param:  map字段同上
    */
    @PostMapping("/update")
    public BaseResponseVO updateNote(@RequestParam Map<String, String> map) {
        noteService.updateNote(map);
        return BaseResponseVO.success();
    }

    /**
    * @description: 修改笔记权限，几乎不用
    * @param:  authority为字符串，只有true和false
    */
    @PostMapping("/changeAuthority")
    public BaseResponseVO changeAuthority(@RequestParam String noteId, @RequestParam String authority) {
        noteService.changeAuthority(noteId, authority);
        return BaseResponseVO.success();
    }

    /**
    * @description: 删除笔记
    * @param:
    */
    @PostMapping("/delete")
    public BaseResponseVO deleteNote(@RequestParam String noteId) {
        noteService.deleteNote(noteId);
        return BaseResponseVO.success();
    }


    /**
    * @description: 查看笔记
    * @param:
    */
    @GetMapping("/findOne")
    public BaseResponseVO findNote(@RequestParam String noteId) {
        String email = AuthUtil.getEmail();
        Note note = noteService.findOneNote(email, noteId);
        return BaseResponseVO.success(note);
    }

    /**
    * @description: 上传图片
    * @param:
    */
    @PostMapping("/uploadPicture")
    public BaseResponseVO uploadPicture(@RequestParam String noteId, @RequestParam MultipartFile file) {
        String uri = noteService.uploadPicture(noteId, file);
        return BaseResponseVO.success(uri);
    }

    /**
    * @description: 查看热门的tag。返回值为set，每个对象字段为 value和score。score为浮点数，取整数后为对应tag的笔记篇数。
    * @param:
    */
    @GetMapping("/hotTag")
    public BaseResponseVO findHotTag() {
        Set<ZSetOperations.TypedTuple<String>> tags = noteService.findHotTag();
        return BaseResponseVO.success(tags);
    }

    /**
    * @description: 查找思维导图
    * @param: keyword关键字，对应思维导图的label。简单的like匹配
    */
    @GetMapping("/searchMindMap")
    public BaseResponseVO searchMindMap(@RequestParam String keyword) {
        List<Knowledge> mindMap = noteService.searchMindMap(keyword);
        return BaseResponseVO.success(mindMap);
    }

    /**
    * @description: 查找我发布的笔记。
    * @param:  page 从1开始，默认1。size默认10。
    */
    @GetMapping("/findMyNote")
    public BaseResponseVO findMyNote(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer size) {
        String email = AuthUtil.getEmail();
        Page<Note> notes = noteService.findMyNote(email, page - 1, size);
        return BaseResponseVO.success(notes);
    }
}
