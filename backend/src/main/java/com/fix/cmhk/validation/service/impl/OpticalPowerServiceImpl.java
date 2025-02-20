package com.fix.cmhk.validation.service.impl;

import com.fix.cmhk.validation.common.BusinessException;
import com.fix.cmhk.validation.model.OpticalPowerResponse;
import com.fix.cmhk.validation.service.OpticalPowerService;
import com.fix.cmhk.validation.service.OpticalPowerRecognitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 光功率识别服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OpticalPowerServiceImpl implements OpticalPowerService {
    
    private final OpticalPowerRecognitionService recognitionService;
    
    @Override
    public OpticalPowerResponse predictOpticalPower(String base64Image) {
        try {
            log.info("开始光功率识别流程");
            
            if (base64Image == null || base64Image.isEmpty()) {
                throw new BusinessException("图片数据不能为空");
            }
            
            log.debug("图片数据长度: {}", base64Image.length());
            OpticalPowerResponse response = recognitionService.recognizeOpticalPower(base64Image);
            
            log.info("光功率识别完成: power={}, valid={}", response.getOpticalPower(), response.isValid());
            return response;
            
        } catch (BusinessException e) {
            log.warn("光功率识别业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("光功率识别系统异常: {}", e.getMessage(), e);
            throw new BusinessException("光功率识别失败: " + e.getMessage());
        }
    }
} 