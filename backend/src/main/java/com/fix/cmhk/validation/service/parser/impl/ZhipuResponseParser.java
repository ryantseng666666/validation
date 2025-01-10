package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.service.parser.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuResponseParser implements ResponseParser<OpticalPowerResponse> {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public OpticalPowerResponse parse(ResponseEntity<String> response) {
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
            
            // 解析JSON字符串为OpticalPowerResponse对象
            JsonNode params = objectMapper.readTree(arguments.asText());
            Double power = params.path("power").asDouble();
            
            OpticalPowerResponse result = OpticalPowerResponse.builder()
                .opticalPower(power)
                .valid(power >= -50 && power <= -15)
                .rawText(response.getBody())
                .build();
            
            log.info("成功解析光功率值: {}dBm", power);
            return result;
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            return OpticalPowerResponse.builder()
                .valid(false)
                .rawText("Error: " + e.getMessage())
                .build();
        }
    }
} 