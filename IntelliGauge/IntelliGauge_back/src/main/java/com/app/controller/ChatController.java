package com.app.controller;

import client.DeepseekClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/deepseek")
public class ChatController {
    @Value("${deepseek.api.key}")
    private String apiKey;

    /**
     * 向DeepSeek-V3提问
     */
    @PostMapping("/ask")
    public String askQuestion(@RequestBody String question) {
        try {
            return new DeepseekClient().getResponse(apiKey, question);
        } catch (IOException e) {
            return "出错了：" + e.getMessage();
        }
    }
}
