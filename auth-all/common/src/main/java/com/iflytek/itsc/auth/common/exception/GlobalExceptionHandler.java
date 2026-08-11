package com.iflytek.itsc.auth.common.exception;

import com.iflytek.itsc.auth.common.response.RestResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 本地版本的GlobalExceptionHandler类，用于解决外部依赖缺失问题
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 基础异常处理方法，可在子类中覆盖
    protected <T> RestResponse<T> handleException(Exception e) {
        return RestResponse.buildError("500", "服务器内部错误");
    }
}