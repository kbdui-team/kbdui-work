package com.app.controller;

import client.DeepseekClient;
import com.app.dto.Question;
import response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/deepseek")
public class ChatController {

    @Value("${deepseek.api.key}")
    private String apiKey;

    private final DeepseekClient deepseekClient = new DeepseekClient();

    /**
     * 向DeepSeek-V3提问
     */
    @PostMapping("/ask")
    public String askQuestion(@RequestBody String question) {
        try {
            return deepseekClient.getResponse(apiKey, question);
        } catch (IOException e) {
            return "出错了：" + e.getMessage();
        }
    }

    /**
     * 生成问题
     */
    @PostMapping("/generate-questions")
    public ApiResponse<List<Question>> generateQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "count", defaultValue = "5") int questionCount,
            @RequestParam(value = "model", defaultValue = "v3") String model) {

        try {
            // 确定使用哪个平台
            boolean useSiliconFlow = "R1".equalsIgnoreCase(model);

            // 调用DeepseekClient生成题目
            List<Question> questions =
                    deepseekClient.generateQuestionsFromFile(apiKey, file, questionCount, useSiliconFlow);

            return ApiResponse.success(questions);
        } catch (Exception e) {
            return ApiResponse.error(500, "题目生成失败: " + e.getMessage());
        }
    }
}
