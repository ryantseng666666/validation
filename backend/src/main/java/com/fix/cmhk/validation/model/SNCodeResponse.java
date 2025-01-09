package com.fix.cmhk.validation.model;

import lombok.Data;

@Data
public class SNCodeResponse {
    private String snCode;
    private String deviceModel;
    private String manufactureDate;
    private boolean isValid;
    private String productSeries;
    private String factory;
    private String warrantyStatus;
    private String batchNumber;
} 