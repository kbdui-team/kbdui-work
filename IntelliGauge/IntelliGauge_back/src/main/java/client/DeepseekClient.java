package client;

import com.app.dto.Question;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.web.multipart.MultipartFile;
import request.DeepseekRequest;
import tool.FileParserUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DeepseekClient {
    //如果使用硅基平台，API_URL修改为 "https://api.siliconflow.cn/v1/chat/completions"
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    // private static final String TENCENT_API_URL = "https://api.lkeap.cloud.tencent.com/v1/chat/completions";

    // 加上超时配置
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    // 用于在 Java 对象和 JSON 数据之间进行转换
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Gson gson = new Gson();

    public String getResponse(String apiKey, String prompt) throws IOException {
        // 构建请求体
        DeepseekRequest.Message message = DeepseekRequest.Message.builder()
                .role("user")
                .content(prompt).build();
        //如果使用硅基平台，model修改为 "deepseek-ai/DeepSeek-V3"
        DeepseekRequest requestBody = DeepseekRequest.builder()
                .model("deepseek-chat")
                .messages(Collections.singletonList(message))
                .build();

        // 创建HTTP请求
        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String getResponse(String apiKey, String prompt, boolean useSiliconFlow) throws IOException {
//        String apiUrl = useSiliconFlow ? SILICON_API_URL : TENCENT_API_URL;
//        String model = useSiliconFlow ? "deepseek-ai/DeepSeek-V3" : "deepseek-chat";
        String model = useSiliconFlow ? "deepseek-reasoner" : "deepseek-chat";

        // 构建请求体
        DeepseekRequest.Message message = DeepseekRequest.Message.builder()
                .role("user")
                .content(prompt).build();

        DeepseekRequest requestBody = DeepseekRequest.builder()
                .model(model)
                .messages(Collections.singletonList(message))
                .temperature(0.7)
                .response_format(Collections.singletonMap("type", "json_object")) // 强制JSON输出
                .build();

        // 创建HTTP请求
//        Request request = new Request.Builder()
//                .url(apiUrl)
//                .post(RequestBody.create(objectMapper.writeValueAsString(requestBody),
//                        MediaType.get("application/json")))
//                .addHeader("Authorization", "Bearer " + apiKey)
//                .build();
        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(gson.toJson(requestBody), MediaType.get("application/json")))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API请求失败: " + response.code() + " - " + response.message());
            }
            return response.body().string();
        }
    }

    // 处理文件并生成题目
    public List<Question> generateQuestionsFromFile(
            String apiKey,
            MultipartFile file,
            int questionCount,
            boolean useSiliconFlow) throws IOException {

        // 1. 解析文件内容
        String content = FileParserUtil.extractTextFromFile(file);

        // 2. 构建Prompt
        String prompt = FileParserUtil.buildQuestionPrompt(content, questionCount);

        // 3. 调用API
        String jsonResponse = getResponse(apiKey, prompt, useSiliconFlow);

        // 4. 解析API响应
        return parseQuestionResponse(jsonResponse);
    }

    // 解析API返回的题目数据
    private List<Question> parseQuestionResponse(String jsonResponse) throws IOException {
        // 解析整个API响应
        JsonNode rootNode = objectMapper.readTree(jsonResponse);

        // 提取content字段（包含题目JSON数组）
        String content = rootNode.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

        // 为什么要这样拆开写呢
        JsonNode questionsNode = objectMapper.readTree(content).path("questions");

        // 解析题目列表
        return objectMapper.readValue(questionsNode.toString(), new TypeReference<List<Question>>() {});
    }
}
