package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.model.request.ImageRequest;
import com.fix.cmhk.validation.service.OpticalPowerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/optical-power")
@RequiredArgsConstructor
public class OpticalPowerController {
    
    private final OpticalPowerService opticalPowerService;
    
    @PostMapping("/predict")
    public ResponseEntity<OpticalPowerResponse> predict(@RequestBody ImageRequest request) {
        log.info("收到光功率识别请求");
        log.debug("输入图片base64长度: {}", request.getBase64Image().length());
        
        OpticalPowerResponse response = opticalPowerService.predictOpticalPower(request.getBase64Image());
        log.info("光功率识别完成: {}", response);
        
        return ResponseEntity.ok(response);
    }
} 