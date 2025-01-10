package com.fix.cmhk.validation.service;

import com.fix.cmhk.validation.model.SNCodeResponse;

public interface SNCodeRecognitionService {
    SNCodeResponse recognizeSNCode(String base64Image);
} 