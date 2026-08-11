package com.iflytek.itsc.auth.resource.manager.common.exception;

import com.iflytek.itsc.auth.common.exception.GlobalExceptionHandler;
import com.iflytek.itsc.auth.common.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Classname ExceptionHandler
 * @Description 全局异常处理
 * @Date 2023/12/15 10:34
 * @Created by wxqiu
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandler extends GlobalExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler({AuthBizException.class})
    public RestResponse<?> handle(AuthBizException e) {
        return RestResponse.buildError(e.getErrorCode(), e.getErrorMsg());
    }
}
