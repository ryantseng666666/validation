package com.fix.cmhk.validation.security;

public class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String[] PUBLIC_URLS = {
            "/api/auth/**",
            "/api/optical-power/**",
            "/api/sn-code/**",
            "/api/speedtest/**",
            "/error"
    };

    private SecurityConstants() {
        throw new IllegalStateException("Utility class");
    }
} 