package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.SpeedTestResponse;
import com.fix.cmhk.validation.service.parser.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuSpeedTestParser implements ResponseParser<SpeedTestResponse> {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public SpeedTestResponse parse(ResponseEntity<String> response) {
        try {
            log.info("开始解析智谱AI响应...");
            JsonNode root = objectMapper.readTree(response.getBody());
            
            // 获取tool_calls数组
            JsonNode toolCalls = root.path("choices").get(0).path("message").path("tool_calls");
            if (toolCalls.isEmpty()) {
                log.error("未找到tool_calls数据");
                throw new RuntimeException("API响应中缺少tool_calls数据");
            }
            
            // 获取第一个tool call的参数
            JsonNode arguments = toolCalls.get(0).path("function").path("arguments");
            if (arguments.isMissingNode()) {
                log.error("未找到arguments数据");
                throw new RuntimeException("API响应中缺少arguments数据");
            }
            
            // 解析JSON字符串为SpeedTestResponse对象
            SpeedTestResponse result = objectMapper.readValue(arguments.asText(), SpeedTestResponse.class);
            result.setMeasurementTime(LocalDateTime.now());
            result.setStatus("SUCCESS");
            
            log.info("成功解析速度测试结果: {}", result);
            return result;
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            SpeedTestResponse errorResponse = new SpeedTestResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setRawText("Error: " + e.getMessage());
            errorResponse.setMeasurementTime(LocalDateTime.now());
            return errorResponse;
        }
    }
} 