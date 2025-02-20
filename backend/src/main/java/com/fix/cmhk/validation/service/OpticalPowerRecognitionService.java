package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.OpticalPowerResponse;

/**
 * 光功率识别服务接口
 */
public interface OpticalPowerRecognitionService {
    /**
     * 识别光功率值
     *
     * @param base64Image Base64编码的图片数据
     * @return 光功率识别结果
     */
    OpticalPowerResponse recognizeOpticalPower(String base64Image);
} 