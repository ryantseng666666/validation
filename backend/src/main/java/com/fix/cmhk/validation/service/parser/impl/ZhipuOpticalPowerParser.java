package com.fix.cmhk.validation.service.parser.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.service.parser.ResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

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
            
            // 创建响应对象
            OpticalPowerResponse result = new OpticalPowerResponse();
            result.setRawText(content);
            
            // 使用正则表达式提取光功率值
            Matcher matcher = powerPattern.matcher(content);
            if (matcher.find()) {
                String powerStr = matcher.group().replace("dBm", "").trim();
                double power = Double.parseDouble(powerStr);
                
                // 验证光功率值是否在有效范围内 (-50 到 -15)
                if (power >= -50 && power <= -15) {
                    result.setOpticalPower(power);
                    result.setValid(true);
                    log.info("成功提取光功率值: {}dBm", power);
                } else {
                    result.setValid(false);
                    log.warn("提取的光功率值超出有效范围: {}dBm", power);
                }
            } else {
                result.setValid(false);
                log.warn("未能从文本中提取出有效的光功率值");
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
            OpticalPowerResponse errorResponse = new OpticalPowerResponse();
            errorResponse.setValid(false);
            errorResponse.setRawText("Error: " + e.getMessage());
            return errorResponse;
        }
    }
} 