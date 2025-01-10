package com.fix.cmhk.validation.service.parser;

import org.springframework.http.ResponseEntity;

/**
 * 响应解析器接口
 * @param <T> 解析结果的类型
 */
public interface ResponseParser<T> {
    /**
     * 解析API响应
     * @param response API响应
     * @return 解析后的结果
     */
    T parse(ResponseEntity<String> response);
} 