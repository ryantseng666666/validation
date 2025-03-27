package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.model.SNCodeResponse;
import com.fix.cmhk.validation.model.request.ImageRequest;
import com.fix.cmhk.validation.service.SNCodeRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/sn-code")
@RequiredArgsConstructor
public class SNCodeController {
    
    private final SNCodeRecognitionService snCodeRecognitionService;
    
    @PostMapping("/predict")
    public ResponseEntity<SNCodeResponse> predict(@RequestBody ImageRequest request) {
        log.info("收到SN码识别请求");
        log.debug("输入图片base64长度: {}", request.getBase64Image().length());
        
        SNCodeResponse response = snCodeRecognitionService.recognizeSNCode(request.getBase64Image());
        log.info("SN码识别完成: {}", response);
        
        return ResponseEntity.ok(response);
    }
} 
