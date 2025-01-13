package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.model.SpeedTestResponse;
import com.fix.cmhk.validation.service.parser.ResponseParser;
import com.fix.cmhk.validation.util.JsonRecoverUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class ZhipuOpticalPowerParser implements ResponseParser<OpticalPowerResponse> {
    
    private final ObjectMapper objectMapper;
    private final Pattern powerPattern = Pattern.compile("-\\d+\\.\\d+\\s*dBm");
    
    @Override
    public OpticalPowerResponse parse(ResponseEntity<String> response) {
        try {
            log.info("开始解析智谱AI响应...");
            JsonNode root = objectMapper.readTree(response.getBody());
            
            // 获取content内容
            String content = root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
                
            log.debug("提取的原始文本内容: {}", content);
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
            // 尝试解析JSON格式的响应
            try {
                JsonNode contentJson = objectMapper.readTree(content);
                if (contentJson.has("power")) {
                    double power = contentJson.get("power").asDouble();
                    return createResponse(power, content, true);
                }
            } catch (Exception e) {
                log.debug("Content不是JSON格式，尝试使用正则表达式解析");
            }
            
            // 使用正则表达式提取光功率值
            Matcher matcher = powerPattern.matcher(content);
            if (matcher.find()) {
                String powerStr = matcher.group().replace("dBm", "").trim();
                double power = Double.parseDouble(powerStr);
                return createResponse(power, content, true);
            }
            
            log.warn("未能从文本中提取出有效的光功率值");
            // return createResponse(null, content, false);
            // 如果JSON解析失败，使用JsonRecoverUtil进行修复
            Map.Entry<String, Map<String, Object>> recoveredJson = JsonRecoverUtil.tryParseJsonObject(content);
            Map<String, Object> jsonMap = recoveredJson.getValue();
            if (jsonMap.containsKey("power")) {
                Object value = jsonMap.get("power");
                double power = 0.0;
                if (value instanceof Number) {
                    power = ((Number) value).doubleValue();
                } else if (value instanceof String) {
                    String powerStr = ((String) value).replaceAll("[^-\\d.]", "");
                    // 使用正则表达式提取数字,包括负号和小数点
                    Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
                    Matcher numberMatcher = pattern.matcher(powerStr);
                    if (numberMatcher.find()) {
                        power = Double.parseDouble(numberMatcher.group());
                    } else {
                        power = 0.0;
                    }
                }
                return createResponse(power, content, true);
            }
            
            return createResponse(null, content, false);
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            return createResponse(null, "Error: " + e.getMessage(), false);
        }
    }
    
    private OpticalPowerResponse createResponse(Double power, String rawText, boolean valid) {
        if (power != null) {
            // 验证光功率值是否在有效范围内 (-50 到 -15)
            valid = power >= -50 && power <= -15;
            if (!valid) {
                log.warn("提取的光功率值超出有效范围: {}dBm", power);
            } else {
                log.info("成功提取光功率值: {}dBm", power);
            }
        }
        
        return OpticalPowerResponse.builder()
            .opticalPower(power)
            .valid(valid)
            .rawText(rawText)
            .build();
    }

    
    
} 
