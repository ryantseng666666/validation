package com.fix.cmhk.validation.model.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateCheckResponse {
    private String status;  // "success" or "fail"
    private String message;
    private Integer count;  // 记录数量
} 