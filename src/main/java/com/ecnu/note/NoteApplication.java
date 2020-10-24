package com.ecnu.note;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author onion
 * @date 2020/1/26 -9:35 上午
 */
@SpringBootApplication
public class NoteApplication {
    public static void main(String[] args) {
        SpringApplication.run(NoteApplication.class, args);
    }
    @Bean
    public RestTemplate get() {
        return new RestTemplate();
    }


}
