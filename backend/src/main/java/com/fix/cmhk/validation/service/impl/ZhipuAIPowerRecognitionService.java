package com.fix.cmhk.validation.service.impl;

import com.fix.cmhk.validation.config.ZhipuAIConfig;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.service.OpticalPowerRecognitionService;
import com.fix.cmhk.validation.service.parser.impl.ZhipuOpticalPowerParser;
import com.fix.cmhk.validation.service.token.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZhipuAIPowerRecognitionService implements OpticalPowerRecognitionService {
    
    private final ZhipuAIConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final TokenGenerator tokenGenerator;
    private final ZhipuOpticalPowerParser responseParser;
    
    @Override
    public OpticalPowerResponse recognizeOpticalPower(String base64Image) {
        try {
            log.info("开始光功率识别流程...");
            log.debug("输入图片base64长度: {}", base64Image.length());
            
            // 生成Token
            String token = tokenGenerator.generateToken();
            log.info("成功生成Token，准备调用API");
            
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(base64Image);
            log.debug("请求体构建完成: {}", requestBody);
            
            // 发送请求
            ResponseEntity<String> response = sendRequest(token, requestBody);
            log.info("收到API响应，状态码: {}", response.getStatusCode());
            log.debug("API响应内容: {}", response.getBody());
            
            // 解析响应
            return responseParser.parse(response);
            
        } catch (Exception e) {
            log.error("光功率识别过程中发生异常: {}", e.getMessage(), e);
            OpticalPowerResponse errorResponse = new OpticalPowerResponse();
            errorResponse.setValid(false);
            errorResponse.setRawText("Error: " + e.getMessage());
            return errorResponse;
        }
    }
    
    private Map<String, Object> buildRequestBody(String base64Image) {
        // 系统消息
        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "你是一个专业的光功率测试数据识别助手。请仔细分析图片中的光功率值，要求：1. 光功率值一定是负数，以'-'号开头，后面跟数字，单位是dBm；2. 数值范围一定在-50到-15之间；3. 如果找到多个数值，请选择最可能是光功率的值；4. 请以JSON格式返回，格式为：{\"power\": 数字}；5. 数字请保留小数点后2位；6. 如果没有找到符合要求的值，返回{\"power\": null}");
        
        // 用户消息
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        
        // 添加图片内容
        List<Map<String, Object>> contentList = new ArrayList<>();
        
        // 添加文本内容
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", "请分析这张图片中的光功率值，并以JSON格式返回数据");
        contentList.add(textContent);
        
        // 添加图片内容
        Map<String, Object> imageUrl = new HashMap<>();
        imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
        
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image_url");
        imageContent.put("image_url", imageUrl);
        contentList.add(imageContent);
        
        userMessage.put("content", contentList);
        
        // 组装消息列表
        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(systemMessage);
        messages.add(userMessage);
        
        // 构建完整请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", config.getModel());
        requestBody.put("messages", messages);
        requestBody.put("stream", false);
        requestBody.put("request_id", String.format("power_%d", System.currentTimeMillis()));
        
        return requestBody;
    }
    
    private ResponseEntity<String> sendRequest(String token, Map<String, Object> requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", token);
        
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
        
        log.info("正在发送请求到API...");
        log.debug("请求URL: {}", config.getApiUrl());
        
        return restTemplate.exchange(
            config.getApiUrl(),
            HttpMethod.POST,
            requestEntity,
            String.class
        );
    }
} 