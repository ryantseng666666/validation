package com.fix.cmhk.validation.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON恢复工具类
 * 用于尝试解析和修复不规范的JSON字符串
 */
@Slf4j
public class JsonRecoverUtil {

    /**
     * ObjectMapper实例，用于JSON解析
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 尝试解析JSON对象字符串
     * @param input 输入的JSON字符串
     * @return 包含原始字符串和解析结果的Map.Entry
     */
    public static Map.Entry<String, Map<String, Object>> tryParseJsonObject(String input) {
        // 首先尝试直接解析JSON
        Map<String, Object> result = null;
        try {
            result = objectMapper.readValue(input, Map.class);
            return new AbstractMap.SimpleEntry<>(input, result);
        } catch (Exception e) {
            log.info("警告: JSON解析错误，尝试修复");
        }

        // 如果解析成功，直接返回结果
        if (result != null) {
            return new AbstractMap.SimpleEntry<>(input, result);
        }

        // 使用正则表达式提取JSON对象
        Pattern pattern = Pattern.compile("\\{(.*)\\}");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = "{" + matcher.group(1) + "}";
        }

        // 清理和修复常见的JSON格式问题
        input = input.replace("{{", "{")
                .replace("}}", "}")
                .replace("\"[{", "[{")
                .replace("}]\"", "}]")
                .replace("\\", " ")
                .replace("\\n", " ")
                .replace("\n", " ")
                .replace("\r", "")
                .trim();

        // 移除Markdown代码块标记
        if (input.startsWith("```")) {
            input = input.substring(3);
        }
        if (input.startsWith("```json")) {
            input = input.substring(7);
        }
        if (input.endsWith("```")) {
            input = input.substring(0, input.length() - 3);
        }

        // 尝试解析修复后的JSON
        try {
            result = objectMapper.readValue(input, Map.class);
            return new AbstractMap.SimpleEntry<>(input, result);
        } catch (Exception e) {
            // 如果还是失败，尝试使用JSONObject解析
            try {
                String jsonInfo = new JSONObject(input).toString();
                result = objectMapper.readValue(jsonInfo, Map.class);
                return new AbstractMap.SimpleEntry<>(jsonInfo, result);
            } catch (Exception ex) {
                // 所有尝试都失败，记录错误并返回空Map
                log.error("JSON加载错误, json={}", input, ex);
                return new AbstractMap.SimpleEntry<>(input, new HashMap<>());
            }
        }
    }
} 