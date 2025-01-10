package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.OpticalPowerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpticalPowerService {
    
    private final OpticalPowerRecognitionService recognitionService;
    
    public OpticalPowerResponse predictOpticalPower(String base64Image) {
        try {
            log.info("开始光功率识别流程...");
            return recognitionService.recognizeOpticalPower(base64Image);
            
        } catch (Exception e) {
            log.error("光功率识别过程中发生异常: {}", e.getMessage(), e);
            OpticalPowerResponse errorResponse = new OpticalPowerResponse();
            errorResponse.setValid(false);
            errorResponse.setRawText("Error: " + e.getMessage());
            return errorResponse;
        }
    }
} 