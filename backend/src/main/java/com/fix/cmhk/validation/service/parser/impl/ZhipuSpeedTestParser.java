package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.SpeedTestResponse;
import com.fix.cmhk.validation.service.parser.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.util.Map;
import com.fix.cmhk.validation.util.JsonRecoverUtil;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            
            // 获取choices数组中的第一个message的content
            String content = root.path("choices").get(0).path("message").path("content").asText();
            log.debug("获取到的content: {}", content);
            // 如果content包含markdown格式的json,需要提取出{}中的内容
            if (content.contains("```json")) {
                String[] parts = content.split("```json");
                if (parts.length > 1) {
                    // 获取json部分
                    String jsonPart = parts[1];
                    // 再次分割去掉结尾的```
                    parts = jsonPart.split("```");
                    if (parts.length > 0) {
                        content = parts[0].trim();
                    }
                }
            }
            log.debug("提取后的content: {}", content);
            // 尝试解析JSON格式的响应
            try {
                JsonNode contentJson = objectMapper.readTree(content);
                if (contentJson.has("uploadSpeed") && contentJson.has("downloadSpeed") && 
                    contentJson.has("referenceId") && contentJson.has("ipAddress")) {
                    return SpeedTestResponse.builder()
                        .uploadSpeed(extractSpeedValue(contentJson.get("uploadSpeed").asText()))
                        .downloadSpeed(extractSpeedValue(contentJson.get("downloadSpeed").asText()))
                        .referenceId(contentJson.get("referenceId").asText())
                        .ipAddress(contentJson.get("ipAddress").asText())
                        .measurementTime(LocalDateTime.now())
                        .status("SUCCESS")
                        .rawText(content)
                        .build();
                }
            } catch (Exception e) {
                log.debug("Content不是JSON格式，尝试使用正则表达式解析");
            }

            //如果JSON解析失败，使用backend/src/main/java/com/fix/cmhk/validation/util/JsonRecoverUtil.java这个工具进行修复
            // 导入所需的类
            Map.Entry<String, Map<String, Object>> recoveredJson = JsonRecoverUtil.tryParseJsonObject(content);
            Map<String, Object> jsonMap = recoveredJson.getValue();
            
            double uploadSpeed = 0.0;
            double downloadSpeed = 0.0;
            String referenceId = "";
            String ipAddress = "";
            
            if (jsonMap.containsKey("uploadSpeed")) {
                Object value = jsonMap.get("uploadSpeed");
                if (value instanceof Number) {
                    uploadSpeed = ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    String speedStr = ((String) value).replaceAll("[^\\d.]", "");
                    uploadSpeed = Double.parseDouble(speedStr);
                }
            }
            
            if (jsonMap.containsKey("downloadSpeed")) {
                Object value = jsonMap.get("downloadSpeed");
                if (value instanceof Number) {
                    downloadSpeed = ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    String speedStr = ((String) value).replaceAll("[^\\d.]", "");
                    downloadSpeed = Double.parseDouble(speedStr);
                }
            }
            
            if (jsonMap.containsKey("referenceId")) {
                referenceId = String.valueOf(jsonMap.get("referenceId"));
            }
            
            if (jsonMap.containsKey("ipAddress")) {
                ipAddress = String.valueOf(jsonMap.get("ipAddress"));
            }
            
            log.info("修复后的JSON解析结果 - 上传速度: {}, 下载速度: {}, 参考ID: {}, IP地址: {}", 
                uploadSpeed, downloadSpeed, referenceId, ipAddress);
            return SpeedTestResponse.builder()
                .uploadSpeed(extractSpeedValue(String.valueOf(uploadSpeed)))
                .downloadSpeed(extractSpeedValue(String.valueOf(downloadSpeed)))
                .referenceId(referenceId)
                .ipAddress(ipAddress)
                .measurementTime(LocalDateTime.now())
                .status("SUCCESS")
                .rawText(content)
                .build();
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            return SpeedTestResponse.builder()
                .status("ERROR")
                .rawText("Error: " + e.getMessage())
                .measurementTime(LocalDateTime.now())
                .build();
        }
    }

    private double extractSpeedValue(String speedText) {
        try {
            return Double.parseDouble(speedText.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            log.warn("解析速度值失败: {}", speedText);
            return 0.0;
        }
    }
} 