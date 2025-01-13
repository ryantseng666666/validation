package com.fix.cmhk.validation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SNCodeResponse {
    private boolean success;
    private String message;
    private String snCode;
} 