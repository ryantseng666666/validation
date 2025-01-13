//package com.fix.cmhk.validation.service.parser.impl;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fix.cmhk.validation.model.SNCodeResponse;
//import com.fix.cmhk.validation.util.JsonRecoverUtil;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class ZhipuSNCodeParser {
//
//    private final ObjectMapper objectMapper;
//
//    public SNCodeResponse parse(ResponseEntity<String> response) {
//        try {
//            log.info("开始解析智谱AI响应...");
//            JsonNode root = objectMapper.readTree(response.getBody());
//            JsonNode choices = root.get("choices");
//
//            if (choices == null || choices.isEmpty()) {
//                log.error("响应中未找到choices字段");
//                return createErrorResponse("响应格式错误：未找到choices字段");
//            }
//
//            JsonNode message = choices.get(0).get("message");
//            if (message == null) {
//                log.error("响应中未找到message字段");
//                return createErrorResponse("响应格式错误：未找到message字段");
//            }
//
//            // 首先尝试从tool_calls中解析
//            JsonNode toolCalls = message.get("tool_calls");
//            if (toolCalls != null && !toolCalls.isEmpty()) {
//                log.debug("从tool_calls中解析结果");
//                return parseFromToolCalls(toolCalls);
//            }
//
//            // 如果没有tool_calls，尝试从content中解析
//            JsonNode content = message.get("content");
//            if (content != null && !content.isNull()) {
//                log.debug("从content中解析结果: {}", content.asText());
//                return parseFromContent(content.asText());
//            }
//
//            log.error("响应中未找到有效内容");
//            return createErrorResponse("响应格式错误：未找到有效内容");
//
//        } catch (Exception e) {
//            log.error("解析智谱AI响应时发生异常: {}", e.getMessage(), e);
//            return createErrorResponse("解析响应失败：" + e.getMessage());
//        }
//    }
//
//    private SNCodeResponse parseFromToolCalls(JsonNode toolCalls) {
//        try {
//            JsonNode firstCall = toolCalls.get(0);
//            if (firstCall.get("function").get("name").asText().equals("extract_sn_code")) {
//                JsonNode arguments = objectMapper.readTree(firstCall.get("function").get("arguments").asText());
//                return SNCodeResponse.builder()
//                    .snCode(arguments.get("snCode").asText())
//                    .valid(arguments.get("valid").asBoolean())
//                    .build();
//            }
//        } catch (Exception e) {
//            log.warn("从tool_calls解析失败: {}", e.getMessage());
//        }
//        return null;
//    }
//
//    private SNCodeResponse parseFromContent(String content) {
//        try {
//            // 首先尝试从内容中提取JSON
//            String jsonStr = JsonRecoverUtil.extractJsonFromText(content);
//            if (jsonStr != null) {
//                JsonNode jsonNode = objectMapper.readTree(jsonStr);
//                if (jsonNode.has("snCode")) {
//                    return SNCodeResponse.builder()
//                        .snCode(jsonNode.get("snCode").asText())
//                        .valid(jsonNode.has("valid") ? jsonNode.get("valid").asBoolean() : true)
//                        .build();
//                }
//            }
//
//            // 如果无法提取JSON，尝试使用正则表达式提取SN码
//            Pattern pattern = Pattern.compile("[A-Z0-9-]{8,}");
//            Matcher matcher = pattern.matcher(content);
//            if (matcher.find()) {
//                String snCode = matcher.group();
//                log.debug("通过正则表达式提取到SN码: {}", snCode);
//                return SNCodeResponse.builder()
//                    .snCode(snCode)
//                    .valid(true)
//                    .build();
//            }
//
//            // 如果仍然无法提取，尝试提取引号中的内容
//            pattern = Pattern.compile("[\"\"'']([^\"\"'']+)[\"\"'']");
//            matcher = pattern.matcher(content);
//            if (matcher.find()) {
//                String snCode = matcher.group(1);
//                log.debug("从引号中提取到SN码: {}", snCode);
//                return SNCodeResponse.builder()
//                    .snCode(snCode)
//                    .valid(true)
//                    .build();
//            }
//
//            log.warn("无法从内容中提取SN码: {}", content);
//            return createErrorResponse("无法从响应中提取SN码");
//
//        } catch (Exception e) {
//            log.error("解析content时发生异常: {}", e.getMessage());
//            return createErrorResponse("解析响应失败：" + e.getMessage());
//        }
//    }
//
//    private SNCodeResponse createErrorResponse(String message) {
//        return SNCodeResponse.builder()
//            .snCode(null)
//            .valid(false)
//            .errorMessage(message)
//            .build();
//    }
//}