package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.SpeedTestResponse;
import com.fix.cmhk.validation.service.impl.ZhipuSpeedTestRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpeedTestService {
    
    private final ZhipuSpeedTestRecognitionService speedTestRecognitionService;
    
    public SpeedTestResponse predictSpeed(String base64Image) {
        try {
            log.info("开始处理速度测试图片识别请求");
            return speedTestRecognitionService.recognizeSpeedTest(base64Image);
        } catch (Exception e) {
            log.error("处理速度测试图片时发生错误", e);
            return SpeedTestResponse.builder()
                .status("ERROR")
                .rawText("Error: " + e.getMessage())
                .build();
        }
    }
} 