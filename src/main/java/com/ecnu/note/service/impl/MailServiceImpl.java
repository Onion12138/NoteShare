package com.ecnu.note.service.impl;

import com.ecnu.note.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * @author onion
 * @date 2020/1/27 -5:45 下午
 */
@Service
@Slf4j
public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.from}")
    private String from;
    @Override
    public void sendMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(content, true);
            mailSender.send(message);
            log.info("邮件已发送给{}", to);
        } catch (MessagingException e) {
            log.error("邮件发送异常!",e);
            throw new RuntimeException(e.getMessage());
        }
    }
    @Override
    public void sendHtmlMail(String to, String subject, String text, Map<String, String> attachmentMap) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        //是否发送的邮件是富文本（附件，图片，html等）
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
        messageHelper.setFrom(from);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setText(text, true);//默认为false，显示原始html代码，无效果
        if(attachmentMap != null){
            attachmentMap.forEach((key, value) -> {
                try {
                    File file = new File(value);
                    if (file.exists()) {
                        messageHelper.addAttachment(key, new FileSystemResource(file));
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }
        log.info("发送邮件给{}", to);
        mailSender.send(mimeMessage);
    }
}
