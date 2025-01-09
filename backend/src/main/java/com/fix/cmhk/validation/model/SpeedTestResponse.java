package com.fix.cmhk.validation.model;

import lombok.Data;

@Data
public class SpeedTestResponse {
    private double downloadSpeed;
    private double uploadSpeed;
    private String referenceNumber;
    private String ipAddress;
} 