package com.fix.cmhk.validation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateCheckResponse {
    private String status;  // "success" or "fail"
    private String message;
    private Integer count;  // 记录数量
} 