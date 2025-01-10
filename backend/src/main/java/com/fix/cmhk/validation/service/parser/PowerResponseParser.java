package com.fix.cmhk.validation.service.parser;

import com.fix.cmhk.validation.model.OpticalPowerResponse;
import org.springframework.http.ResponseEntity;

public interface PowerResponseParser {
    /**
     * 解析API响应
     * @param response API响应
     * @return 光功率识别结果
     */
    OpticalPowerResponse parse(ResponseEntity<String> response);
} 