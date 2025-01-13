package com.fix.cmhk.validation.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * 通义千问工具类
 */
@Component
public class QwenUtil {
    private static final Logger logger = LoggerFactory.getLogger(QwenUtil.class);
    
    @Value("${qwen.api.key}")
    private String apiKey;
    
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    private static final String MULTIMODAL_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/multimodal-conversation/generation";
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();

    private static final String PROMPT = 
            "please extract SN from the input ONT images.\n" +
            "\n" +
            "The SN value is typically a combination of letters and numbers, which may contain hyphens.\n" + 
            "You should always follow the instructions and output a valid JSON object.\n" +
            "\n" +
            "only return the json format schema, which could be inferred by the following OpticalPowerInfo class\n" +
            "\n" +
            "public class SNInfo {\n" +  
            "    String SN;\n" +  
            "}\n" +  
            "\n" +
            "If you do not recognize any value or recognize any value with low confidence,\n" + 
            "set the corresponding value NA for SN.";

    /**
     * 处理图片并识别SN码
     *
     * @param base64Image base64编码的图片
     * @return 识别到的SN码
     */
    public String processSNImage(String base64Image) {
        try {
            if (base64Image == null || base64Image.trim().isEmpty()) {
                logger.error("base64图片数据不能为空");
                return "NA";
            }
            
            return getSNFromImage(base64Image);
        } catch (Exception e) {
            logger.error("处理图片失败: {}", e.getMessage(), e);
            return "NA";
        }
    }

    private String getSNFromImage(String base64Image) throws IOException {
        // 调用通义千问模型
        String qwenResponse = qwenRecoV1(base64Image);
        if (qwenResponse == null) {
            logger.error("通义千问模型返回为空");
            return "NA";
        }
        
        // 构造消息
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", qwenResponse);

        List<Map<String, Object>> messages = Collections.singletonList(message);
        
        // 调用模型获取结果
        JSONObject response = getResponse(messages);
        
        if (response != null) {
            try {
                JSONObject assistantOutput = response.getJSONObject("output")
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message");

                if (assistantOutput.containsKey("tool_calls")) {
                    JSONObject toolCall = assistantOutput.getJSONArray("tool_calls")
                            .getJSONObject(0)
                            .getJSONObject("function");
                    
                    if ("printSNInfo".equals(toolCall.getString("name"))) {
                        JSONObject args = JSON.parseObject(toolCall.getString("arguments"));
                        String sn = args.getString("SN");
                        if (sn != null && !sn.trim().isEmpty()) {
                            return sn;
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("解析模型响应失败: {}", e.getMessage(), e);
            }
        }
        
        return "NA";
    }

    private String qwenRecoV1(String base64Image) throws IOException {
        Map<String, Object> content = new HashMap<>();
        content.put("image", "data:image/jpeg;base64," + base64Image);
        content.put("text", PROMPT);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", Collections.singletonList(content));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-vl-max");
        requestBody.put("messages", Collections.singletonList(message));

        Request request = new Request.Builder()
                .url(MULTIMODAL_URL)
                .addHeader("Authorization", "Bearer " + this.apiKey)
                .post(RequestBody.create(JSON_MEDIA_TYPE, JSON.toJSONString(requestBody)))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("请求失败: {}, 状态码: {}", response.message(), response.code());
                throw new IOException("请求失败: " + response);
            }
            
            String responseBody = response.body().string();
            JSONObject jsonResponse = JSON.parseObject(responseBody);
            return Optional.ofNullable(jsonResponse)
                    .map(jr -> jr.getJSONObject("output"))
                    .map(output -> output.getJSONArray("choices"))
                    .map(choices -> choices.getJSONObject(0))
                    .map(choice -> choice.getJSONObject("message"))
                    .map(message1 -> message1.getJSONArray("content"))
                    .map(content1 -> content1.getJSONObject(0))
                    .map(content1 -> content1.getString("text"))
                    .orElse(null);
        }
    }

    private JSONObject getResponse(List<Map<String, Object>> messages) throws IOException {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "qwen-max");
        
        Map<String, Object> input = new HashMap<>();
        input.put("messages", messages);
        requestBody.put("input", input);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("result_format", "message");
        parameters.put("tools", getTools());
        requestBody.put("parameters", parameters);

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + this.apiKey)
                .post(RequestBody.create(JSON_MEDIA_TYPE, JSON.toJSONString(requestBody)))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("请求失败: {}, 状态码: {}", response.message(), response.code());
                throw new IOException("请求失败: " + response);
            }
            return JSON.parseObject(response.body().string());
        }
    }

    private static List<Map<String, Object>> getTools() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> sn = new HashMap<>();
        sn.put("description", "SN");
        sn.put("type", "String");
        properties.put("SN", sn);
        parameters.put("properties", properties);
        parameters.put("required", Collections.singletonList("SN"));

        Map<String, Object> function = new HashMap<>();
        function.put("name", "printSNInfo");
        function.put("description", "SN码,打印SN信息");
        function.put("parameters", parameters);

        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");
        tool.put("function", function);

        return Collections.singletonList(tool);
    }
} 