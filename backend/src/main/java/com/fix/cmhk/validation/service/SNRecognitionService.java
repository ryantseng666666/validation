package com.fix.cmhk.validation.service;

import org.springframework.web.multipart.MultipartFile;

public interface SNRecognitionService {
    String recognizeSNCode(MultipartFile image);
} 