package com.fix.cmhk.validation.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 统一API响应结果包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseResult<T> {
    private Integer code;      // 状态码
    private String message;    // 提示信息
    private T data;           // 响应数据
    private Long timestamp;    // 时间戳

    /**
     * 成功响应
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(200, "Success", data, System.currentTimeMillis());
    }

    /**
     * 失败响应
     */
    public static <T> ResponseResult<T> error(Integer code, String message) {
        return new ResponseResult<>(code, message, null, System.currentTimeMillis());
    }
} 