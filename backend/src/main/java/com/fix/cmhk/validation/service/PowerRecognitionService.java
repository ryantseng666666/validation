package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.OpticalPowerResponse;

public interface PowerRecognitionService {
    /**
     * 识别图片中的光功率值
     * @param base64Image Base64编码的图片数据
     * @return 光功率识别结果
     */
    OpticalPowerResponse recognizePower(String base64Image);
} 