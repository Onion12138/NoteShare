package com.ecnu.note.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.ecnu.note.domain.mongo.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;

/**
 * @author onion
 * @date 2020/1/28 -10:56 上午
 */
@Slf4j
public class JwtUtil {
    private static final String KEY = "notehub";
    private static final long ttl = 60 * 60 * 24 * 1000 * 24;
    public static String createJwt(User user){
        //设置头信息
        HashMap<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return JWT.create()
                .withHeader(header)
                .withKeyId(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + ttl))
                .sign(Algorithm.HMAC256(KEY));
    }
    public static String parseJwt(String jwtStr){
        Algorithm algorithm = Algorithm.HMAC256(KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT jwt = verifier.verify(jwtStr);
        return jwt.getKeyId();
    }
}
