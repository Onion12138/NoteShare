package com.ecnu.note.utils;

import com.qiniu.util.Auth;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author onion
 * @date 2020/2/12 -4:45 下午
 */
public class DownloadUtil {
    public static String getFileUrl(String filename, String accessKey, String secretKey, Long expireInSeconds){
        String domainOfBucket = "http://ecnuonion.club";
        String encodedFileName = URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }
}
