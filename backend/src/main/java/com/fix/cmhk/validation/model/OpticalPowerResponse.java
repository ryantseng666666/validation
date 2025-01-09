package com.fix.cmhk.validation.model;

import lombok.Data;

@Data
public class OpticalPowerResponse {
    private double powerValue;
    private String measurementTime;
    private String deviceModel;
    private String status;
} 