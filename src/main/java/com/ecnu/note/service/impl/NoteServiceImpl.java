package com.ecnu.note.service.impl;

import com.ecnu.note.dao.KnowledgeDao;
import com.ecnu.note.dao.NoteDao;
import com.ecnu.note.dao.SearchDao;
import com.ecnu.note.domain.mongo.Knowledge;
import com.ecnu.note.domain.mongo.Note;
import com.ecnu.note.domain.search.NoteSearch;
import com.ecnu.note.service.NoteService;
import com.ecnu.note.utils.DownloadUtil;
import com.ecnu.note.utils.KeyUtil;
import com.ecnu.note.utils.UuidUtil;
import com.google.gson.Gson;
import com.hankcs.hanlp.HanLP;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author onion
 * @date 2020/1/31 -2:29 下午
 */
@Service
@Slf4j
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteDao noteDao;

    @Autowired
    private SearchDao searchDao;

    @Autowired
    private KnowledgeDao knowledgeDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${qiniu.access-key}")
    private String accessKey;
    @Value("${qiniu.secret-key}")
    private String secretKey;
    @Value("${qiniu.bucket}")
    private String bucket;
    @Value("2592000")
    private long expireInSeconds;

    @Override
    public String publishNote(Map<String, String> map) {
        String id = KeyUtil.getUniqueKey();
        String email = map.get("authorEmail");
        String forkFrom = map.get("forkFrom");
        Note note = Note.builder()
                .id(id)
                .authorEmail(email)
                .title(map.get("title"))
                .authority(map.get("authority").equals("true"))
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .tag(map.get("tag"))
                .summary(getSummary(map.get("content")))
                .content(map.get("content"))
                .valid(true)
                .forkFrom(map.get("forkFrom"))
                .star(0)
                .hate(0)
                .view(0)
                .collect(0)
                .fork(0)
                .build();
        if (!StringUtils.isEmpty(forkFrom)) {
            incField(forkFrom, "fork");
            redisTemplate.opsForHash().put(email + " : fork", new Date().toString(), id);
        }
        noteDao.save(note);
        NoteSearch noteSearch = new NoteSearch();
        noteSearch.setTitle(note.getTitle());
        noteSearch.setCreateTime(note.getCreateTime().toString());
        noteSearch.setUpdateTime(note.getUpdateTime().toString());
        noteSearch.setEmail(note.getAuthorEmail());
        noteSearch.setSummary(note.getSummary());
        noteSearch.setTag(note.getTag());
        note.setId(id);
        searchDao.save(noteSearch);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BoundZSetOperations<String, String> zset = redisTemplate.boundZSetOps("tag");
        zset.incrementScore(note.getTag(),1.0);
        redisTemplate.opsForHash().put(email + " : publish", sdf.format(new Date()), id);
        return id;
    }

    private String getSummary(String document) {
        return String.join(" ", HanLP.extractSummary(document, 10));
    }

    @Override
    public void updateNote(Map<String, String> map) {
        String id = map.get("id");
        Note note = findById(id);
        note.setTitle(map.get("title"));
        note.setContent(map.get("content"));
        note.setUpdateTime(LocalDateTime.now());
        note.setSummary(getSummary(map.get("content")));
        noteDao.save(note);
//        NoteSearch noteSearch = NoteSearch.builder()
//                .title(note.getTitle())
//                .createTime(note.getCreateTime().toString())
//                .updateTime(note.getUpdateTime().toString())
//                .email(note.getAuthorEmail())
//                .summary(note.getSummary())
//                .tag(note.getTag())
//                .id(id)
//                .build();
//        new Thread(() -> searchDao.save(noteSearch)).start();
    }

    @Override
    public void deleteNote(String noteId) {
        updateField(noteId, "valid", false);
    }

    @Override
    public Note findOneNote(String email, String noteId) {
        Note note = findById(noteId);
        if (!note.getValid()) {
            throw new RuntimeException("笔记已经被删除");
        }
        incField(noteId, "view");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        redisTemplate.opsForHash().put(email + " : view", sdf.format(new Date()), noteId);
        return note;
    }


    @Override
    public void changeAuthority(String noteId, String authority) {
        updateField(noteId, "authority", "write".equals(authority));
    }

    @Override
    public void starOrHate(String type, String noteId, String email) {
        incField(noteId, type);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        redisTemplate.opsForHash().put(email + " : " + type, sdf.format(new Date()), noteId);
    }

    @Override
    public String uploadPicture(String noteId, MultipartFile file) {
        InputStream fileInputStream = null;
        try {
            fileInputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        String key = noteId + UuidUtil.getUuid();
        Configuration cfg = new Configuration(Region.region2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        try {
            Response response = uploadManager.put(fileInputStream, key, upToken, null, null);
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            throw new RuntimeException("上传失败");
        }
        return DownloadUtil.getFileUrl(key, accessKey, secretKey, expireInSeconds);
    }


    @Override
    public List<Note> findByAuthor(String email) {
        List<Note> notes = noteDao.findByAuthorEmail(email);
        notes.removeIf(e->!e.getValid());
        return notes;
    }

    @Override
    public Set<ZSetOperations.TypedTuple<String>> findHotTag() {
        return redisTemplate.opsForZSet().rangeWithScores("tag", 0, - 1);
    }

    @Override
    public List<Knowledge> searchMindMap(String keyword) {
        return knowledgeDao.findAllByTitleLikeAndShareIsTrue(keyword);
    }

    @Override
    public Page<Note> findMyNote(String email, Integer page, Integer size) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(email + " : publish");
        List<String> ids = entries.values().stream().map(e -> (String) e).collect(Collectors.toList());
        return noteDao.findAllByIdIn(ids, PageRequest.of(page, size));
    }

    private void incField(String noteId, String field) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(noteId));
        Update update = new Update();
        update.inc(field, 1);
        mongoTemplate.updateFirst(query, update, Note.class);
    }

    private void updateField(String noteId, String field, Object value) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(noteId));
        Update update = new Update();
        update.set(field, value);
        mongoTemplate.updateFirst(query, update, Note.class);
    }

    private Note findById(String noteId) {
        Optional<Note> optional = noteDao.findById(noteId);
        if (optional.isEmpty()) {
            throw new RuntimeException("笔记不存在");
        }
        return optional.get();
    }

}
