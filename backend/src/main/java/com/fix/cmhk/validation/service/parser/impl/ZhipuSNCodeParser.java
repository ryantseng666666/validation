package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.SNCodeResponse;
import com.fix.cmhk.validation.service.parser.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuSNCodeParser implements ResponseParser<SNCodeResponse> {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public SNCodeResponse parse(ResponseEntity<String> response) {
        try {
            log.info("开始解析智谱AI响应...");
            JsonNode root = objectMapper.readTree(response.getBody());
            
            // 获取choices数组
            JsonNode choices = root.path("choices");
            if (choices.isEmpty()) {
                log.error("未找到choices数据");
                throw new RuntimeException("API响应中缺少choices数据");
            }
            
            // 获取第一个choice的message内容
            JsonNode message = choices.get(0).path("message");
            if (message.isMissingNode()) {
                log.error("未找到message数据");
                throw new RuntimeException("API响应中缺少message数据");
            }
            
            // 获取content内容
            String content = message.path("content").asText();
            
            // 尝试从content中解析JSON
            try {
                JsonNode contentJson = objectMapper.readTree(content);
                if (contentJson.has("snCode")) {
                    String snCode = contentJson.get("snCode").asText();
                    log.info("从content中成功解析SN码: {}", snCode);
                    return SNCodeResponse.builder()
                        .snCode(snCode)
                        .build();
                }
            } catch (Exception e) {
                log.debug("content不是JSON格式，尝试从tool_calls中解析");
            }
            
            // 如果content中没有找到，尝试从tool_calls中解析
            JsonNode toolCalls = message.path("tool_calls");
            if (!toolCalls.isEmpty() && toolCalls.isArray()) {
                JsonNode firstToolCall = toolCalls.get(0);
                JsonNode function = firstToolCall.path("function");
                JsonNode arguments = function.path("arguments");
                
                if (!arguments.isMissingNode()) {
                    JsonNode functionResult = objectMapper.readTree(arguments.asText());
                    if (functionResult.has("snCode")) {
                        String snCode = functionResult.get("snCode").asText();
                        log.info("从tool_calls中成功解析SN码: {}", snCode);
                        return SNCodeResponse.builder()
                            .snCode(snCode)
                            .build();
                    }
                }
            }
            
            // 如果都没有找到，尝试从content文本中提取
            if (!content.isEmpty()) {
                String snCode = extractSNFromText(content);
                if (snCode != null) {
                    log.info("从content文本中成功提取SN码: {}", snCode);
                    return SNCodeResponse.builder()
                        .snCode(snCode)
                        .build();
                }
            }
            
            log.error("无法从响应中解析出SN码");
            throw new RuntimeException("无法从响应中解析出SN码");
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            return SNCodeResponse.builder()
                .snCode("Error: " + e.getMessage())
                .build();
        }
    }
    
    private String extractSNFromText(String content) {
        // 尝试从文本中提取SN码
        // 1. 尝试"SN:"格式
        if (content.contains("SN:")) {
            String[] parts = content.split("SN:");
            if (parts.length > 1) {
                String sn = parts[1].trim().split("\\s+")[0];
                if (!sn.isEmpty()) {
                    return sn;
                }
            }
        }
        
        // 2. 尝试"SN："格式（中文冒号）
        if (content.contains("SN：")) {
            String[] parts = content.split("SN：");
            if (parts.length > 1) {
                String sn = parts[1].trim().split("\\s+")[0];
                if (!sn.isEmpty()) {
                    return sn;
                }
            }
        }
        
        return null;
    }
} 