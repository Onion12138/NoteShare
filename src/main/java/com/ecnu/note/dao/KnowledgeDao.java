package com.ecnu.note.dao;

import com.ecnu.note.domain.mongo.Knowledge;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author onion
 * @date 2020/10/24 -10:30 上午
 */
public interface KnowledgeDao extends MongoRepository<Knowledge, String> {
    List<Knowledge> findAllByEmail(String email);
    List<Knowledge> findAllByTitleLikeAndShareIsTrue(String title);
}
