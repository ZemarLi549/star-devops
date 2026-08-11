package com.iflytek.itsc.auth.resource.manager.common.config;

import com.iflytek.itsc.auth.resource.manager.common.filter.XssFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class FilterConfig {
    @Value("${xss.excludes:/**/*.ico,/**/*.png,/**/*.jpg,/**/*.gif,/**/*.css,/**/*.js,/**/*.html}")
    private String xssExcludes;

    @Bean
    public FilterRegistrationBean xssFilterRegistration() {
        // 负责对请求进行登录验证拦截
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.setOrder(0);
        registration.setEnabled(true);
        registration.addUrlPatterns("/*");
        Map<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", xssExcludes);
        registration.setInitParameters(initParameters);
        return registration;
    }

}
