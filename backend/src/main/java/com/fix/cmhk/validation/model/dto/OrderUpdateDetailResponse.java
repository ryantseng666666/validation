package com.fix.cmhk.validation.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderUpdateDetailResponse {
    private Long id;
    private String jobNo;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private LocalDateTime updateTime;
    private String updateBy;
} 