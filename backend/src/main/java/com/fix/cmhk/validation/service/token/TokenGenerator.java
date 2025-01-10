package com.fix.cmhk.validation.service.token;

public interface TokenGenerator {
    /**
     * 生成API访问Token
     * @return 生成的Token字符串
     */
    String generateToken();
} 