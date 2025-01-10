package com.fix.cmhk.validation.service.token.impl;

import com.fix.cmhk.validation.config.ZhipuAIConfig;
import com.fix.cmhk.validation.service.token.TokenGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuTokenGenerator implements TokenGenerator {
    
    private final ZhipuAIConfig config;
    private final ObjectMapper objectMapper;
    
    @Override
    public String generateToken() {
        try {
            log.debug("开始生成JWT Token...");
            
            // 解析API Key
            String[] parts = config.getApiKey().split("\\.");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid API key format");
            }
            String id = parts[0];
            String secret = parts[1];
            
            // 准备JWT头部
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("sign_type", "SIGN");
            String headerStr = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsString(header).getBytes(StandardCharsets.UTF_8));
            
            // 准备JWT载荷
            long timestamp = System.currentTimeMillis() / 1000;
            Map<String, Object> payload = new HashMap<>();
            payload.put("api_key", id);
            payload.put("exp", timestamp + 3600);
            payload.put("timestamp", timestamp);
            String payloadStr = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8));
            
            // 生成签名
            String signContent = headerStr + "." + payloadStr;
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(sha256Hmac.doFinal(signContent.getBytes(StandardCharsets.UTF_8)));
            
            String token = signContent + "." + signature;
            log.debug("JWT Token生成成功");
            return token;
            
        } catch (Exception e) {
            log.error("生成JWT Token时发生错误: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token: " + e.getMessage());
        }
    }
} 