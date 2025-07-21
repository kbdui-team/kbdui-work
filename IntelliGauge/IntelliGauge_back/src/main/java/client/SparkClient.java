package client;

import java.io.*;
import java.net.URISyntaxException;
import java.security.SignatureException;
import java.util.HashMap;
import org.apache.commons.lang.StringEscapeUtils;

import cn.hutool.json.JSONUtil;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sign.LfasrSignature;
import tool.HttpUtil;

import javax.annotation.PostConstruct;


@Component
public class SparkClient {
    private static final String HOST = "https://raasr.xfyun.cn";

    @Value("${iflytek.api.apiId}")
    private String appid;

    @Value("${iflytek.api.secret_key}")
    private String keySecret;

    private static final Gson gson = new Gson();


    public String transcribe(File audio) throws IOException, SignatureException, InterruptedException {
        String result = this.upload(audio);
        String jsonStr = StringEscapeUtils.unescapeJavaScript(result);
        String orderId = String.valueOf(JSONUtil.getByPath(JSONUtil.parse(jsonStr), "content.orderId"));
        String fullResult = this.getResult(orderId);
        
        // 解析JSON结果，提取转写文本
        try {
            System.out.println("原始转写结果: " + fullResult);
            
            // 尝试多种JSON结构解析
            String transcribedText = parseTranscriptionResult(fullResult);
            if (transcribedText != null && !transcribedText.trim().isEmpty()) {
                System.out.println("成功提取转写文本: " + transcribedText);
                return transcribedText;
            } else {
                throw new RuntimeException("转写结果为空或格式异常: " + fullResult);
            }
        } catch (Exception e) {
            throw new RuntimeException("解析转写结果失败: " + e.getMessage() + ", 原始结果: " + fullResult);
        }
    }

    private String upload(File audio) throws SignatureException, FileNotFoundException {
        HashMap<String, Object> map = new HashMap<>(16);
        String fileName = audio.getName();
        long fileSize = audio.length();
        map.put("appId", appid);
        map.put("fileSize", fileSize);
        map.put("fileName", fileName);
        map.put("duration", "200");
        LfasrSignature lfasrSignature = new LfasrSignature(appid, keySecret);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());

        String paramString = HttpUtil.parseMapToPathParam(map);
        System.out.println("upload paramString:" + paramString);

        String url = HOST + "/v2/api/upload" + "?" + paramString;
        System.out.println("upload_url:" + url);
        String response = HttpUtil.iflyrecUpload(url, new FileInputStream(audio));

        System.out.println("upload response:" + response);
        return response;
    }

    private String getResult(String orderId) throws SignatureException, InterruptedException, IOException {
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("orderId", orderId);
        LfasrSignature lfasrSignature = new LfasrSignature(appid, keySecret);
        map.put("signa", lfasrSignature.getSigna());
        map.put("ts", lfasrSignature.getTs());
        map.put("appId", appid);
        map.put("resultType", "transfer,predict");
        String paramString = HttpUtil.parseMapToPathParam(map);
        String url = HOST + "/v2/api/getResult" + "?" + paramString;
        System.out.println("\nget_result_url:" + url);
        while (true) {
            String response = HttpUtil.iflyrecGet(url);
            JsonParse jsonParse = gson.fromJson(response, JsonParse.class);
            if (jsonParse.content.orderInfo.status == 4 || jsonParse.content.orderInfo.status == -1) {
                System.out.println("订单完成:" + response);
                // write(response); // 注释掉文件写入，避免路径问题
                return response;
            } else {
                System.out.println("进行中...，状态为:" + jsonParse.content.orderInfo.status);
                //建议使用回调的方式查询结果，查询接口有请求频率限制
                Thread.sleep(7000);
            }
        }
    }

    /**
     * 灵活解析转写结果，支持讯飞API的多层嵌套JSON结构
     */
    private static String parseTranscriptionResult(String jsonResult) {
        try {
            // 首先解析顶层JSON
            JsonParse jsonParse = gson.fromJson(jsonResult, JsonParse.class);
            if (jsonParse != null && jsonParse.content != null && 
                jsonParse.content.orderResult != null) {
                
                // orderResult是一个JSON字符串，需要再次解析
                String orderResultStr = jsonParse.content.orderResult;
                System.out.println("解析orderResult字符串: " + orderResultStr.substring(0, Math.min(200, orderResultStr.length())) + "...");
                
                // 解析orderResult JSON字符串
                @SuppressWarnings("unchecked")
                HashMap<String, Object> orderResultMap = gson.fromJson(orderResultStr, HashMap.class);
                if (orderResultMap != null) {
                    
                    StringBuilder transcribedText = new StringBuilder();
                    
                    // 尝试解析lattice数组
                    if (orderResultMap.containsKey("lattice")) {
                        Object latticeObj = orderResultMap.get("lattice");
                        if (latticeObj instanceof java.util.List) {
                            @SuppressWarnings("unchecked")
                            java.util.List<Object> lattice = (java.util.List<Object>) latticeObj;
                            
                            System.out.println("解析lattice数组，包含 " + lattice.size() + " 个元素");
                            
                            // 遍历lattice数组，提取每个json_1best中的转写文本
                            for (Object latticeItem : lattice) {
                                if (latticeItem instanceof HashMap) {
                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Object> item = (HashMap<String, Object>) latticeItem;
                                    
                                    // 检查json_1best字段，可能是字符串或对象
                                    Object json1bestObj = item.get("json_1best");
                                    if (json1bestObj != null) {
                                        String json1bestStr;
                                        
                                        if (json1bestObj instanceof String) {
                                            // 如果是字符串，直接使用
                                            json1bestStr = (String) json1bestObj;
                                        } else {
                                            // 如果是对象，转换为JSON字符串
                                            json1bestStr = gson.toJson(json1bestObj);
                                        }
                                        
                                        System.out.println("解析json_1best: " + json1bestStr.substring(0, Math.min(100, json1bestStr.length())) + "...");
                                        
                                        // 解析json_1best字符串
                                        try {
                                            @SuppressWarnings("unchecked")
                                            HashMap<String, Object> json1bestMap = gson.fromJson(json1bestStr, HashMap.class);
                                            
                                            if (json1bestMap != null && json1bestMap.containsKey("st")) {
                                                Object stObj = json1bestMap.get("st");
                                                if (stObj instanceof HashMap) {
                                                    @SuppressWarnings("unchecked")
                                                    HashMap<String, Object> st = (HashMap<String, Object>) stObj;
                                                    
                                                    if (st.containsKey("rt")) {
                                                        Object rtObj = st.get("rt");
                                                        if (rtObj instanceof java.util.List) {
                                                            @SuppressWarnings("unchecked")
                                                            java.util.List<Object> rt = (java.util.List<Object>) rtObj;
                                                            
                                                            System.out.println("找到rt数组，包含 " + rt.size() + " 个元素");
                                                            
                                                            // 提取每个rt中的词
                                                            for (Object rtItem : rt) {
                                                                if (rtItem instanceof HashMap) {
                                                                    @SuppressWarnings("unchecked")
                                                                    HashMap<String, Object> rtMap = (HashMap<String, Object>) rtItem;
                                                                    
                                                                    if (rtMap.containsKey("ws")) {
                                                                        Object wsObj = rtMap.get("ws");
                                                                        if (wsObj instanceof java.util.List) {
                                                                            @SuppressWarnings("unchecked")
                                                                            java.util.List<Object> ws = (java.util.List<Object>) wsObj;
                                                                            
                                                                            System.out.println("找到ws数组，包含 " + ws.size() + " 个元素");
                                                                            
                                                                            for (Object wsItem : ws) {
                                                                                if (wsItem instanceof HashMap) {
                                                                                    @SuppressWarnings("unchecked")
                                                                                    HashMap<String, Object> wsMap = (HashMap<String, Object>) wsItem;
                                                                                    
                                                                                    if (wsMap.containsKey("cw")) {
                                                                                        Object cwObj = wsMap.get("cw");
                                                                                        if (cwObj instanceof java.util.List) {
                                                                                            @SuppressWarnings("unchecked")
                                                                                            java.util.List<Object> cw = (java.util.List<Object>) cwObj;
                                                                                            
                                                                                            System.out.println("找到cw数组，包含 " + cw.size() + " 个元素");
                                                                                            
                                                                                            for (Object cwItem : cw) {
                                                                                                if (cwItem instanceof HashMap) {
                                                                                                    @SuppressWarnings("unchecked")
                                                                                                    HashMap<String, Object> cwMap = (HashMap<String, Object>) cwItem;
                                                                                                    
                                                                                                    if (cwMap.containsKey("w")) {
                                                                                                        String word = (String) cwMap.get("w");
                                                                                                        // 过滤掉空字符串
                                                                                                        if (word != null && !word.trim().isEmpty()) {
                                                                                                            System.out.println("提取到词汇: " + word);
                                                                                                            transcribedText.append(word);
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("解析json_1best失败: " + e.getMessage());
                                            // 如果解析失败，尝试直接提取字符串中的词汇
                                            String extractedWords = extractWordsFromString(json1bestStr);
                                            if (extractedWords != null && !extractedWords.trim().isEmpty()) {
                                                System.out.println("字符串提取成功: " + extractedWords);
                                                transcribedText.append(extractedWords);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // 如果lattice没有结果，尝试解析lattice2数组
                    if (transcribedText.length() == 0 && orderResultMap.containsKey("lattice2")) {
                        Object lattice2Obj = orderResultMap.get("lattice2");
                        if (lattice2Obj instanceof java.util.List) {
                            @SuppressWarnings("unchecked")
                            java.util.List<Object> lattice2 = (java.util.List<Object>) lattice2Obj;
                            
                            System.out.println("解析lattice2数组，包含 " + lattice2.size() + " 个元素");
                            
                            // 遍历lattice2数组，提取每个json_1best中的转写文本
                            for (Object latticeItem : lattice2) {
                                if (latticeItem instanceof HashMap) {
                                    @SuppressWarnings("unchecked")
                                    HashMap<String, Object> item = (HashMap<String, Object>) latticeItem;
                                    
                                    // 检查json_1best字段，可能是字符串或对象
                                    Object json1bestObj = item.get("json_1best");
                                    if (json1bestObj != null) {
                                        String json1bestStr;
                                        
                                        if (json1bestObj instanceof String) {
                                            // 如果是字符串，直接使用
                                            json1bestStr = (String) json1bestObj;
                                        } else {
                                            // 如果是对象，转换为JSON字符串
                                            json1bestStr = gson.toJson(json1bestObj);
                                        }
                                        
                                        System.out.println("解析lattice2 json_1best: " + json1bestStr.substring(0, Math.min(100, json1bestStr.length())) + "...");
                                        
                                        // 解析json_1best字符串
                                        try {
                                            @SuppressWarnings("unchecked")
                                            HashMap<String, Object> json1bestMap = gson.fromJson(json1bestStr, HashMap.class);
                                            
                                            if (json1bestMap != null && json1bestMap.containsKey("st")) {
                                                Object stObj = json1bestMap.get("st");
                                                if (stObj instanceof HashMap) {
                                                    @SuppressWarnings("unchecked")
                                                    HashMap<String, Object> st = (HashMap<String, Object>) stObj;
                                                    
                                                    if (st.containsKey("rt")) {
                                                        Object rtObj = st.get("rt");
                                                        if (rtObj instanceof java.util.List) {
                                                            @SuppressWarnings("unchecked")
                                                            java.util.List<Object> rt = (java.util.List<Object>) rtObj;
                                                            
                                                            // 提取每个rt中的词
                                                            for (Object rtItem : rt) {
                                                                if (rtItem instanceof HashMap) {
                                                                    @SuppressWarnings("unchecked")
                                                                    HashMap<String, Object> rtMap = (HashMap<String, Object>) rtItem;
                                                                    
                                                                    if (rtMap.containsKey("ws")) {
                                                                        Object wsObj = rtMap.get("ws");
                                                                        if (wsObj instanceof java.util.List) {
                                                                            @SuppressWarnings("unchecked")
                                                                            java.util.List<Object> ws = (java.util.List<Object>) wsObj;
                                                                            
                                                                            for (Object wsItem : ws) {
                                                                                if (wsItem instanceof HashMap) {
                                                                                    @SuppressWarnings("unchecked")
                                                                                    HashMap<String, Object> wsMap = (HashMap<String, Object>) wsItem;
                                                                                    
                                                                                    if (wsMap.containsKey("cw")) {
                                                                                        Object cwObj = wsMap.get("cw");
                                                                                        if (cwObj instanceof java.util.List) {
                                                                                            @SuppressWarnings("unchecked")
                                                                                            java.util.List<Object> cw = (java.util.List<Object>) cwObj;
                                                                                            
                                                                                            for (Object cwItem : cw) {
                                                                                                if (cwItem instanceof HashMap) {
                                                                                                    @SuppressWarnings("unchecked")
                                                                                                    HashMap<String, Object> cwMap = (HashMap<String, Object>) cwItem;
                                                                                                    
                                                                                                    if (cwMap.containsKey("w")) {
                                                                                                        String word = (String) cwMap.get("w");
                                                                                                        // 过滤掉空字符串
                                                                                                        if (word != null && !word.trim().isEmpty()) {
                                                                                                            transcribedText.append(word);
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            System.out.println("解析lattice2 json_1best失败: " + e.getMessage());
                                            // 如果解析失败，尝试直接提取字符串中的词汇
                                            String extractedWords = extractWordsFromString(json1bestStr);
                                            if (extractedWords != null && !extractedWords.trim().isEmpty()) {
                                                transcribedText.append(extractedWords);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    String result = transcribedText.toString();
                    if (!result.trim().isEmpty()) {
                        System.out.println("成功提取转写文本: " + result);
                        return result;
                    }
                }
            }
            
            // 如果上述方法失败，尝试备用方法
            System.out.println("主要解析方法失败，尝试备用方法...");
            return parseTranscriptionResultBackup(jsonResult);
            
        } catch (Exception e) {
            System.out.println("JSON解析异常: " + e.getMessage());
            return parseTranscriptionResultBackup(jsonResult);
        }
    }
    
    /**
     * 从字符串中直接提取词汇
     */
    private static String extractWordsFromString(String jsonStr) {
        try {
            // 使用正则表达式提取"w"字段的值
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"w\":\"([^\"]+)\"");
            java.util.regex.Matcher matcher = pattern.matcher(jsonStr);
            
            StringBuilder result = new StringBuilder();
            while (matcher.find()) {
                String word = matcher.group(1);
                if (word != null && !word.trim().isEmpty()) {
                    result.append(word);
                }
            }
            
            return result.toString();
        } catch (Exception e) {
            System.out.println("字符串词汇提取失败: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 备用解析方法，使用正则表达式提取文本
     */
    private static String parseTranscriptionResultBackup(String jsonResult) {
        try {
            // 首先尝试从orderResult字段中提取
            java.util.regex.Pattern orderResultPattern = java.util.regex.Pattern.compile("\"orderResult\":\"([^\"]+)\"");
            java.util.regex.Matcher orderResultMatcher = orderResultPattern.matcher(jsonResult);
            
            if (orderResultMatcher.find()) {
                String orderResultStr = orderResultMatcher.group(1);
                System.out.println("从orderResult提取字符串: " + orderResultStr.substring(0, Math.min(200, orderResultStr.length())) + "...");
                
                // 对orderResult字符串进行转义处理
                String unescapedStr = orderResultStr.replace("\\\\", "\\").replace("\\\"", "\"");
                System.out.println("转义后的字符串: " + unescapedStr.substring(0, Math.min(200, unescapedStr.length())) + "...");
                
                // 使用正则表达式提取"w"字段的值
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\"w\":\"([^\"]+)\"");
                java.util.regex.Matcher matcher = pattern.matcher(unescapedStr);
                
                StringBuilder result = new StringBuilder();
                while (matcher.find()) {
                    String word = matcher.group(1);
                    if (word != null && !word.trim().isEmpty()) {
                        result.append(word);
                    }
                }
                
                String transcribedText = result.toString();
                if (!transcribedText.trim().isEmpty()) {
                    System.out.println("备用方法成功提取转写文本: " + transcribedText);
                    return transcribedText;
                }
            }
            
            // 如果上面的方法失败，尝试直接从整个JSON中提取
            System.out.println("尝试直接从整个JSON中提取...");
            
            // 使用更宽松的正则表达式，处理转义字符
            java.util.regex.Pattern globalPattern = java.util.regex.Pattern.compile("\"w\":\"([^\"]*)\"");
            java.util.regex.Matcher globalMatcher = globalPattern.matcher(jsonResult);
            
            StringBuilder globalResult = new StringBuilder();
            while (globalMatcher.find()) {
                String word = globalMatcher.group(1);
                if (word != null && !word.trim().isEmpty()) {
                    globalResult.append(word);
                }
            }
            
            String globalTranscribedText = globalResult.toString();
            if (!globalTranscribedText.trim().isEmpty()) {
                System.out.println("全局提取方法成功提取转写文本: " + globalTranscribedText);
                return globalTranscribedText;
            }
            
            // 最后尝试：直接从原始JSON中提取，不处理转义
            System.out.println("尝试最终提取方法...");
            java.util.regex.Pattern finalPattern = java.util.regex.Pattern.compile("\\\\\"w\\\\\":\\\\\"([^\\\\\"]*)\\\\\"");
            java.util.regex.Matcher finalMatcher = finalPattern.matcher(jsonResult);
            
            StringBuilder finalResult = new StringBuilder();
            while (finalMatcher.find()) {
                String word = finalMatcher.group(1);
                if (word != null && !word.trim().isEmpty()) {
                    finalResult.append(word);
                }
            }
            
            String finalTranscribedText = finalResult.toString();
            if (!finalTranscribedText.trim().isEmpty()) {
                System.out.println("最终提取方法成功提取转写文本: " + finalTranscribedText);
                return finalTranscribedText;
            }
            
            System.out.println("所有解析方法都失败，原始内容: " + jsonResult);
            return null;
            
        } catch (Exception e) {
            System.out.println("备用解析方法异常: " + e.getMessage());
            return null;
        }
    }

    // 临时添加一个空的write方法，避免调用错误
    public static void write(String resp) throws IOException {
        System.out.println("跳过文件写入，响应内容: " + resp.substring(0, Math.min(100, resp.length())) + "...");
    }

    /**
     * 根据路径获取Map中的值
     */
    private static String getValueByPath(HashMap<String, Object> map, String path) {
        try {
            String[] keys = path.split("\\.");
            Object current = map;
            
            for (String key : keys) {
                if (current instanceof HashMap) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> currentMap = (HashMap<String, Object>) current;
                    current = currentMap.get(key);
                } else {
                    return null;
                }
            }
            
            return current != null ? current.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    static class JsonParse {
        Content content;
    }

    static class Content {
        OrderInfo orderInfo;
        String orderResult;  // 添加orderResult字段
    }

    static class OrderInfo {
        Integer status;
        String result;
    }
}
