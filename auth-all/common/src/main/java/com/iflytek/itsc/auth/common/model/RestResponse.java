package com.iflytek.itsc.auth.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {
    
    /**
     * 状态码
     */
    private int code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功响应
     */
    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(200, "success", data);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> RestResponse<T> success() {
        return new RestResponse<>(200, "success", null);
    }
    
    /**
     * 错误响应
     */
    public static <T> RestResponse<T> error(int code, String message) {
        return new RestResponse<>(code, message, null);
    }
    
    /**
     * 错误响应（默认错误码）
     */
    public static <T> RestResponse<T> error(String message) {
        return new RestResponse<>(500, message, null);
    }
}