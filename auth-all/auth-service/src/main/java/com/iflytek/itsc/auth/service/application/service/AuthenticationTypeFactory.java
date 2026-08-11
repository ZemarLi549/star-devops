package com.iflytek.itsc.auth.service.application.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Classname AuthenticationTypeFactory
 * @Description 获取运行时登录方式认证HandlerImpl
 * @Date 2023/12/29 10:46
 * @Created by wxqiu
 */
public class AuthenticationTypeFactory {

    private static Map<String, AuthenticationHandler> strategyMap = new HashMap<>();



    public static AuthenticationHandler getAuthenticationHandlerImpl(String type){
        return strategyMap.get(type);
    }

    /**
     * 注册
     * @param type
     * @param handler
     */
    public static void register(String type, AuthenticationHandler handler){
        strategyMap.put(type,handler);
    }
}
