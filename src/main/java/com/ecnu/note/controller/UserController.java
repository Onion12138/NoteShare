package com.ecnu.note.controller;

import com.ecnu.note.domain.MindMap;
import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.service.UserService;
import com.ecnu.note.utils.AuthUtil;
import com.ecnu.note.vo.BaseResponseVO;
import com.ecnu.note.vo.LoginVO;
import com.ecnu.note.vo.RecordVO;
import com.ecnu.note.vo.RegisterVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -9:38 上午
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponseVO register(@RequestBody RegisterVO registerVO) {
        userService.register(registerVO);
        return BaseResponseVO.success();
    }

    @PostMapping("/login")
    public BaseResponseVO login(@RequestBody LoginVO loginVO){
        Map<String, Object> map = userService.login(loginVO);
        return BaseResponseVO.success(map);
    }

    @PostMapping("/uploadProfile")
    public BaseResponseVO uploadProfile(@RequestParam MultipartFile file) {
        String email = AuthUtil.getEmail();
        String url = userService.uploadProfile(email, file);
        return BaseResponseVO.success(url);
    }

    @GetMapping("/sendCode")
    public BaseResponseVO sendCode(@RequestParam String email) {
        userService.sendCode(email);
        return BaseResponseVO.success();
    }

    @GetMapping("/getMindMap")
    public BaseResponseVO findMyMindMap() {
        String email = AuthUtil.getEmail();
        List<Knowledge> knowledge = userService.findMyMindMap(email);
        return BaseResponseVO.success(knowledge);
    }

    @PostMapping("/addMindMap")
    public BaseResponseVO addMindMap(@RequestBody MindMap mindMap) {
        String email = AuthUtil.getEmail();
        String id = userService.addMindMap(email, mindMap);
        return BaseResponseVO.success(id);
    }

    @PostMapping("/updateMindMap")
    public BaseResponseVO updateMindMap(@RequestBody MindMap mindMap, @RequestParam String id){
        String email = AuthUtil.getEmail();
        userService.updateMindMap(email, mindMap, id);
        return BaseResponseVO.success();
    }

    @PostMapping("/shareMindMap")
    public BaseResponseVO shareMindMap(@RequestParam String id){
        String email = AuthUtil.getEmail();
        userService.shareMindMap(email, id);
        return BaseResponseVO.success();
    }

    @PostMapping("/deleteMindMap")
    public BaseResponseVO deleteMindMap(@RequestParam String id) {
        String email = AuthUtil.getEmail();
        userService.deleteMindMap(email, id);
        return BaseResponseVO.success();
    }

    @GetMapping("/myRecord")
    public BaseResponseVO findMyRecord(@RequestParam String type) {
        String email = AuthUtil.getEmail();
        List<RecordVO> recordVOList = userService.findMyRecord(type, email);
        return BaseResponseVO.success(recordVOList);
    }
}

