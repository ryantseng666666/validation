package com.fix.cmhk.validation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeedTestResponse {
    private Double uploadSpeed;
    private Double downloadSpeed;
    private String referenceId;
    private String ipAddress;
    private LocalDateTime measurementTime;
    private String rawText;
    private String status;
} 