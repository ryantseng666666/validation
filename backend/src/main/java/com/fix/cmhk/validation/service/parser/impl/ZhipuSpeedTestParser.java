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
            
            // 尝试解析JSON格式的响应
            try {
                JsonNode contentJson = objectMapper.readTree(content);
                if (contentJson.has("uploadSpeed") && contentJson.has("downloadSpeed") && 
                    contentJson.has("referenceId") && contentJson.has("ipAddress")) {
                    return SpeedTestResponse.builder()
                        .uploadSpeed(contentJson.get("uploadSpeed").asDouble())
                        .downloadSpeed(contentJson.get("downloadSpeed").asDouble())
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
            
            // 如果JSON解析失败，使用正则表达式提取数据
            Pattern uploadPattern = Pattern.compile("上传速度[：:](\\d+(\\.\\d+)?)\\s*Mbps");
            Pattern downloadPattern = Pattern.compile("下载速度[：:](\\d+(\\.\\d+)?)\\s*Mbps");
            Pattern refIdPattern = Pattern.compile("参考编号[：:](\\S+)");
            Pattern ipPattern = Pattern.compile("IP地址[：:](\\S+)");
            
            Matcher uploadMatcher = uploadPattern.matcher(content);
            Matcher downloadMatcher = downloadPattern.matcher(content);
            Matcher refIdMatcher = refIdPattern.matcher(content);
            Matcher ipMatcher = ipPattern.matcher(content);
            
            Double uploadSpeed = uploadMatcher.find() ? Double.parseDouble(uploadMatcher.group(1)) : null;
            Double downloadSpeed = downloadMatcher.find() ? Double.parseDouble(downloadMatcher.group(1)) : null;
            String referenceId = refIdMatcher.find() ? refIdMatcher.group(1) : null;
            String ipAddress = ipMatcher.find() ? ipMatcher.group(1) : null;
            
            return SpeedTestResponse.builder()
                .uploadSpeed(uploadSpeed)
                .downloadSpeed(downloadSpeed)
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
} 