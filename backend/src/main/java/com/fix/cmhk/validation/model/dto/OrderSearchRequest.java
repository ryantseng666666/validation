package com.fix.cmhk.validation.model.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Data
public class OrderSearchRequest {
    private String jobNo;
    private String customerOrderId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
    
    private Integer autoSuccess;
    
    private Integer page = 0;
    private Integer size = 10;
} 