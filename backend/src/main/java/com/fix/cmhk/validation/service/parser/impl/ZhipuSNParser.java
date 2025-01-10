package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.SNCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuSNParser {
    private final ObjectMapper objectMapper;

    public SNCodeResponse parse(ResponseEntity<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.get("choices");
            
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode firstChoice = choices.get(0);
                JsonNode message = firstChoice.get("message");
                JsonNode toolCalls = message.get("tool_calls");
                
                if (toolCalls != null && toolCalls.isArray() && toolCalls.size() > 0) {
                    JsonNode firstToolCall = toolCalls.get(0);
                    JsonNode function = firstToolCall.get("function");
                    JsonNode arguments = function.get("arguments");
                    
                    // 解析function返回的JSON字符串
                    JsonNode functionResult = objectMapper.readTree(arguments.asText());
                    String snCode = functionResult.get("snCode").asText();
                    
                    return SNCodeResponse.builder()
                        .snCode(snCode)
                        .build();
                }
            }
            
            log.error("无法从响应中解析出SN码");
            return SNCodeResponse.builder()
                .snCode("Error: 无法从响应中解析出SN码")
                .build();
            
        } catch (Exception e) {
            log.error("解析响应时发生异常: {}", e.getMessage(), e);
            return SNCodeResponse.builder()
                .snCode("Error: " + e.getMessage())
                .build();
        }
    }
} 