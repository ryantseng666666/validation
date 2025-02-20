package com.fix.cmhk.validation.controller;

import com.fix.cmhk.validation.common.ResponseResult;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.model.request.ImageRequest;
import com.fix.cmhk.validation.service.OpticalPowerService;
import org.springdoc.api.annotations.ParameterObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 光功率识别控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/optical-power")
@RequiredArgsConstructor
public class OpticalPowerController {
    
    private final OpticalPowerService opticalPowerService;
    
    /**
     * 识别光功率
     * 
     * @param request 图片识别请求
     * @return 光功率识别结果
     */
    @PostMapping("/predict")
    public ResponseResult<OpticalPowerResponse> predict(@Validated @RequestBody @ParameterObject ImageRequest request) {
        log.info("收到光功率识别请求");
        log.debug("输入图片base64长度: {}", request.getBase64Image().length());
        
        OpticalPowerResponse response = opticalPowerService.predictOpticalPower(request.getBase64Image());
        log.info("光功率识别完成: power={}, valid={}", response.getOpticalPower(), response.isValid());
        
        return ResponseResult.success(response);
    }
} 