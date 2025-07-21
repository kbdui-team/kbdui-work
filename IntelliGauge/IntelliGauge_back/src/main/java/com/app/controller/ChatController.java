package com.app.controller;

import client.DeepseekClient;
import client.SparkClient;
import com.app.dto.Question;
import response.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    /**
     * 生成问题(音频)
     */
    @PostMapping("/generate-questions-from-audio")
    public ApiResponse<List<Question>> generateQuestionsFromAudio(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "count", defaultValue = "5") int questionCount,
            @RequestParam(value = "model", defaultValue = "v3") String model) {

        try {
            // 1. 将MultipartFile转换为File
            File tempFile = convertMultipartFileToFile(audioFile);
            
            // 2. 调用SparkClient进行音频转写
            String transcribedText = SparkClient.transcribe(tempFile);
            
            // 3. 检查转写结果
            if (transcribedText == null || transcribedText.trim().isEmpty()) {
                return ApiResponse.error(500, "音频转写失败：未获取到转写内容");
            }
            
            // 4. 确定使用哪个平台
            boolean useSiliconFlow = "R1".equalsIgnoreCase(model);
            
            // 5. 构建生成问题的Prompt
            String prompt = buildQuestionPromptFromText(transcribedText, questionCount);
            
            // 6. 调用DeepseekClient生成题目
            String jsonResponse = deepseekClient.getResponse(apiKey, prompt, useSiliconFlow);
            
            // 7. 解析返回的题目
            List<Question> questions = deepseekClient.parseQuestionResponse(jsonResponse);
            
            // 8. 清理临时文件
            tempFile.delete();
            
            return ApiResponse.success(questions);
            
        } catch (Exception e) {
            return ApiResponse.error(500, "音频转写并生成问题失败: " + e.getMessage());
        }
    }

    /**
     * 将MultipartFile转换为File
     */
    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        // 创建临时文件
        File tempFile = File.createTempFile("audio_", "_" + multipartFile.getOriginalFilename());
        // 将MultipartFile的内容复制到临时文件
        multipartFile.transferTo(tempFile);
        return tempFile;
    }

    /**
     * 根据转写文本构建生成问题的Prompt
     */
    private String buildQuestionPromptFromText(String text, int questionCount) {
        return String.format(
            "请根据以下文本内容生成%d道选择题，要求：\n" +
            "1. 题目要基于文本内容，不能脱离文本\n" +
            "2. 每道题有4个选项，其中只有1个正确答案\n" +
            "3. 题目难度适中，涵盖文本的主要知识点\n" +
            "4. 返回JSON格式，包含questions数组\n\n" +
            "文本内容：\n%s\n\n" +
            "请返回如下JSON格式：\n" +
            "{\n" +
            "  \"questions\": [\n" +
            "    {\n" +
            "      \"question\": \"题目内容\",\n" +
            "      \"options\": [\"选项A\", \"选项B\", \"选项C\", \"选项D\"],\n" +
            "      \"correctAnswer\": 0\n" +
            "    }\n" +
            "  ]\n" +
            "}", 
            questionCount, text
        );
    }
}
