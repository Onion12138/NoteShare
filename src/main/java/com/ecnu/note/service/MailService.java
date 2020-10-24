package com.ecnu.note.service;

import javax.mail.MessagingException;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -5:46 下午
 */
public interface MailService {
    void sendMail(String to, String subject,String content);
    void sendHtmlMail(String to, String subject, String text, Map<String, String> attachmentMap) throws MessagingException;
}
