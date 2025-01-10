package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.SpeedTestResponse;

public interface SpeedTestRecognitionService {
    /**
     * 识别图片中的速度测试结果
     * @param base64Image Base64编码的图片数据
     * @return 速度测试识别结果
     */
    SpeedTestResponse recognizeSpeedTest(String base64Image);
} 