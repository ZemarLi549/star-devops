package com.iflytek.itsc.web.response;

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
    public static <T> RestResponse<T> buildSuccess(T data) {
        return new RestResponse<>(200, "success", data);
    }
    
    /**
     * 成功响应（无数据）
     */
    public static <T> RestResponse<T> buildSuccess() {
        return new RestResponse<>(200, "success", null);
    }
    
    /**
     * 错误响应
     */
    public static <T> RestResponse<T> buildError(int code, String message) {
        return new RestResponse<>(code, message, null);
    }
    
    /**
     * 错误响应（默认错误码）
     */
    public static <T> RestResponse<T> buildError(String message) {
        return new RestResponse<>(500, message, null);
    }
}