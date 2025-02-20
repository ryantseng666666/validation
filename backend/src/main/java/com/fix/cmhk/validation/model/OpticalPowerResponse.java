package com.fix.cmhk.validation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 光功率识别响应类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpticalPowerResponse {
    
    /**
     * 光功率值
     */
    private Double opticalPower;
    
    /**
     * 是否有效
     */
    private boolean valid;
    
    /**
     * 原始文本
     */
    private String rawText;
} 