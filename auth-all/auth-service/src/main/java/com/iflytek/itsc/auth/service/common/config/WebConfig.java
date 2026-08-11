package com.iflytek.itsc.auth.service.common.config;

import com.iflytek.itsc.auth.service.interceptor.AuthorizationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Classname AuthorizationInterceptor
 * @Description 授权拦截器配置
 * @Date 2023/12/18 14:24
 * @Created by wxqiu
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final String LOGIN_MODE_PATH = "/loginMode";

    @Autowired
    private AuthorizationInterceptor authorizationInterceptor;

    @Value("${inner.auth.excludeUrls}")
    private String excludeUrls;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration interceptorRegistration = registry.addInterceptor(authorizationInterceptor);
        interceptorRegistration.addPathPatterns("/**");
        interceptorRegistration.excludePathPatterns(buildExcludePaths());
    }

    private String[] buildExcludePaths() {
        Set<String> paths = new LinkedHashSet<>();
        if (excludeUrls != null) {
            for (String item : excludeUrls.split(",")) {
                if (item != null && !item.trim().isEmpty()) {
                    paths.add(item.trim());
                }
            }
        }
        paths.add(LOGIN_MODE_PATH);
        return paths.toArray(new String[0]);
    }
}
