package com.fix.cmhk.validation.security;

public class SecurityConstants {
    public static final String LOGIN_URL = "/api/auth/login";
    public static final String REGISTER_URL = "/api/auth/register";
    public static final String VERIFY_CODE_URL = "/api/auth/verify-code";
    public static final String SPEED_TEST_URL = "/api/speedtest/predict";
    public static final String OPTICAL_POWER_URL = "/api/optical-power/predict";
    public static final String SN_CODE_URL = "/api/sn-code/predict";
    
    public static final String[] PUBLIC_URLS = {
        LOGIN_URL,
        REGISTER_URL,
        VERIFY_CODE_URL,
        SPEED_TEST_URL,
        OPTICAL_POWER_URL,
        SN_CODE_URL
    };

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
} 