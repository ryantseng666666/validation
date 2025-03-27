package com.fix.cmhk.validation.util;

import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    
    /**
     * 获取当前登录用户的用户名
     * @return 当前用户名，如果未登录则返回"SYSTEM"
     */
    public String getCurrentUsername() {
        // For now, return a default value. In a real application, this would get the authenticated user
        return "system";
    }
} 