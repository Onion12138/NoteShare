package com.ecnu.note.service.impl;

import com.ecnu.note.dao.KnowledgeDao;
import com.ecnu.note.dao.NoteDao;
import com.ecnu.note.dao.UserDao;
import com.ecnu.note.domain.MindMap;
import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.domain.mongo.Note;
import com.ecnu.note.domain.mongo.User;
import com.ecnu.note.service.MailService;
import com.ecnu.note.service.UserService;
import com.ecnu.note.utils.*;
import com.ecnu.note.vo.LoginVO;
import com.ecnu.note.vo.RecordVO;
import com.ecnu.note.vo.RegisterVO;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author onion
 * @date 2020/1/27 -5:50 下午
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private KnowledgeDao knowledgeDao;

    @Autowired
    private NoteDao noteDao;

    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("31536000")
    private long expireInSeconds;

    @Override
    public void register(@Valid RegisterVO registerVO)  {
        if (userDao.findById(registerVO.getEmail()).isPresent()) {
            throw new RuntimeException("邮箱已经被占用");
        }
        String redisCode = redisTemplate.opsForValue().get("code_" + registerVO.getEmail());
        if (redisCode == null) {
            throw new RuntimeException("验证码不存在");
        }
        if (!registerVO.getCode().equals(redisCode)) {
            throw new RuntimeException("验证码错误");
        }
        String salt = SaltUtil.getSalt();
        String password = Md5Util.encrypt(registerVO.getPassword() + salt);
        User user = User.builder()
                .email(registerVO.getEmail())
                .username(registerVO.getUsername())
                .registerTime(LocalDate.now())
                .password(password)
                .salt(salt)
                .profileUrl("https://avatars2.githubusercontent.com/u/33611404?s=400&v=4")
                .disabled(false)
                .build();
        userDao.save(user);
    }

    @Override
    public Map<String, Object> login(LoginVO loginVO) {
        String email = loginVO.getEmail();
        Optional<User> optionalUser = userDao.findById(email);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }
        User user = optionalUser.get();
        if (user.getDisabled()) {
            throw new RuntimeException("用户被禁用");
        }
        String salt = user.getSalt();
        String rawPassword = loginVO.getPassword();
        if (!user.getPassword().equals(Md5Util.encrypt(rawPassword + salt))) {
            throw new RuntimeException("密码错误");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("token", JwtUtil.createJwt(user));
        map.put("email", user.getEmail());
        map.put("username", user.getUsername());
        map.put("profileUrl", user.getProfileUrl());
        return map;
    }

    @Override
    public String uploadProfile(String email, MultipartFile file){
        InputStream fileInputStream = null;
        try {
            fileInputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        String key = email + UuidUtil.getUuid();
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(fileInputStream, key, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            log.info("upload file : {}", putRet);
        } catch (QiniuException ex) {
            throw new RuntimeException("上传失败");
        }
        String url = DownloadUtil.getFileUrl(key, accessKey, secretKey, expireInSeconds);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(email));
        Update update = new Update();
        update.set("profileUrl", url);
        mongoTemplate.updateFirst(query, update, User.class);
        return url;
    }


    @Override
    public void sendCode(String email) {
        String code = SaltUtil.getCode();
        redisTemplate.opsForValue().set("code_" + email, code, 10, TimeUnit.MINUTES);
        String content = "这是你的验证码：" + code + ",此验证码10分钟内有效!" + email;
        new Thread(() -> mailService.sendMail(email,"notehub验证码", content)).start();
    }


    @Override
    public String addMindMap(String email, MindMap mindMap) {
        Knowledge knowledge = Knowledge.builder()
                .email(email)
                .id(UuidUtil.getUuid())
                .share(false)
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .title(mindMap.getLabel())
                .mindMap(mindMap)
                .build();
        knowledgeDao.save(knowledge);
        return knowledge.getId();
    }

    @Override
    public List<Knowledge> findMyMindMap(String email) {
        return knowledgeDao.findAllByEmail(email);
    }

    @Override
    public void shareMindMap(String email, String id) {
        Optional<Knowledge> optional = knowledgeDao.findById(id);
        if (optional.isPresent()) {
            Knowledge knowledge = optional.get();
            knowledge.setShare(true);
            knowledgeDao.save(knowledge);
        }
    }

    @Override
    public void updateMindMap(String email, MindMap mindMap, String id) {
        Optional<Knowledge> optional = knowledgeDao.findById(id);
        if (optional.isPresent()) {
            Knowledge knowledge = optional.get();
            if (!knowledge.getEmail().equals(email)) {
                throw new RuntimeException("权限不足");
            }
            knowledge.setMindMap(mindMap);
            knowledge.setTitle(mindMap.getLabel());
            knowledge.setUpdateTime(LocalDateTime.now());
            knowledgeDao.save(knowledge);
        }
    }

    @Override
    public void deleteMindMap(String email, String id) {
        Optional<Knowledge> optional = knowledgeDao.findById(id);
        if (optional.isPresent()) {
            Knowledge knowledge = optional.get();
            if (!knowledge.getEmail().equals(email)) {
                throw new RuntimeException("权限不足");
            }
            knowledgeDao.delete(knowledge);
        }
    }

    @Override
    public List<RecordVO> findMyRecord(String type, String email) {
        String key = email + " : " + type;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        List<RecordVO> recordVOList = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            RecordVO recordVO = new RecordVO();
            recordVO.setId((String) entry.getValue());
            Date date = new Date((String) entry.getValue());
            recordVO.setTime(date);
            recordVO.setNote(noteDao.findById(recordVO.getId()).orElse(Note.builder().build()));
            recordVOList.add(recordVO);
        }
        return recordVOList;
    }


}
