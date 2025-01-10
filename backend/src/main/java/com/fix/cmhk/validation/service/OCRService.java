package com.fix.cmhk.validation.service;

/**
 * OCR服务接口，用于处理图像识别相关功能
 */
public interface OCRService {
    /**
     * 从图片中提取文本内容
     *
     * @param base64Image Base64编码的图片数据
     * @return 提取的文本内容
     */
    String extractText(String base64Image);
} 