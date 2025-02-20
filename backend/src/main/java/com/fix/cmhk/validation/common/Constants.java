package com.fix.cmhk.validation.common;

/**
 * 系统常量类
 */
public class Constants {
    
    /**
     * 响应状态码
     */
    public static class ResponseCode {
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
        public static final int UNAUTHORIZED = 401;
        public static final int FORBIDDEN = 403;
        public static final int NOT_FOUND = 404;
        public static final int INTERNAL_ERROR = 500;
    }
    
    /**
     * 业务常量
     */
    public static class Business {
        public static final double MIN_OPTICAL_POWER = -50.0;
        public static final double MAX_OPTICAL_POWER = -15.0;
        public static final int MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    }
} 