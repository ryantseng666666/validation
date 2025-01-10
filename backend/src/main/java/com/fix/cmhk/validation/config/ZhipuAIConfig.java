package com.fix.cmhk.validation.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "zhipu.ai")
public class ZhipuAIConfig {
    private String apiKey;
    private String apiUrl;
    private String model;
    
    // 速度测试相关配置
    private String speedTestPrompt;
    private String speedTestFunctionName;
    private String speedTestFunctionDescription;
    
    // 光功率相关配置
    private String opticalPowerPrompt;
    private String opticalPowerFunctionName;
    private String opticalPowerFunctionDescription;
    
    // SN码相关配置
    private String snCodePrompt;
    private String snCodeFunctionName;
    private String snCodeFunctionDescription;
} 