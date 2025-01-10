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
            
            // 获取tool_calls数组
            JsonNode toolCalls = message.path("tool_calls");
            if (toolCalls.isEmpty()) {
                log.error("未找到tool_calls数据");
                throw new RuntimeException("API响应中缺少tool_calls数据");
            }
            
            // 获取第一个tool call的function参数
            JsonNode function = toolCalls.get(0).path("function");
            JsonNode arguments = function.path("arguments");
            if (arguments.isMissingNode()) {
                log.error("未找到arguments数据");
                throw new RuntimeException("API响应中缺少arguments数据");
            }
            
            // 解析JSON字符串为SNCodeResponse对象
            JsonNode functionResult = objectMapper.readTree(arguments.asText());
            String snCode = functionResult.path("snCode").asText();
            
            if (snCode.isEmpty()) {
                log.error("未找到snCode数据");
                throw new RuntimeException("API响应中缺少snCode数据");
            }
            
            log.info("成功解析SN码识别结果: {}", snCode);
            return SNCodeResponse.builder()
                .snCode(snCode)
                .build();
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            return SNCodeResponse.builder()
                .snCode("Error: " + e.getMessage())
                .build();
        }
    }
} 