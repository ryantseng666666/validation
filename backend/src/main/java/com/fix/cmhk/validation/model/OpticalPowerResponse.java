package com.fix.cmhk.validation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpticalPowerResponse {
    private Double opticalPower;
    private boolean valid;
    private String rawText;
} 