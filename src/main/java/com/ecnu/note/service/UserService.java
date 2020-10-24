package com.ecnu.note.service;

import com.ecnu.note.domain.MindMap;
import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.vo.LoginVO;
import com.ecnu.note.vo.RecordVO;
import com.ecnu.note.vo.RegisterVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -5:49 下午
 */
public interface UserService {

    void register(RegisterVO registerVO);

    Map<String, Object> login(LoginVO loginVO);

    String uploadProfile(String email, MultipartFile file);

    void sendCode(String email);

    void updateMindMap(String email, MindMap mindMap, String id);

    String addMindMap(String email, MindMap mindMap);

    List<Knowledge> findMyMindMap(String email);

    void shareMindMap(String email, String id);

    void deleteMindMap(String email, String id);

    List<RecordVO> findMyRecord(String type, String email);
}
