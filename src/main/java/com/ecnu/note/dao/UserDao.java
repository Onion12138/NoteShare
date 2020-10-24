package com.ecnu.note.dao;

import com.ecnu.note.domain.mongo.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author onion
 * @date 2020/1/27 -10:56 上午
 */
public interface UserDao extends MongoRepository<User, String> {
}
