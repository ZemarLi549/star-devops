package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xdkong2
 * @date 2023/12/21
 * @desc 处理Filter等处异常，因RestControllerAdvice无法捕获，需重定义/error地址，让RestControllerAdvice捕获
 **/
@Controller
public class ErrorControllerImpl implements ErrorController {

    @RequestMapping("/error")
    public void handleError(HttpServletRequest request) throws Throwable {
        if (request.getAttribute("javax.servlet.error.exception") != null) {
            throw (Throwable) request.getAttribute("javax.servlet.error.exception");
        }
    }
}
