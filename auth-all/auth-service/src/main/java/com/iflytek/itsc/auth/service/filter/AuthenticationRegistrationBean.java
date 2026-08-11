package com.iflytek.itsc.auth.service.filter;

import com.iflytek.itsc.auth.service.application.service.AuthenticationHandler;
import com.iflytek.itsc.auth.service.application.service.AuthenticationTypeFactory;
import com.iflytek.itsc.auth.service.service.ResourceUserService;
import com.iflytek.itsc.auth.service.common.constants.Constant;
import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.common.cache.CacheStoreService;
import com.iflytek.itsc.auth.common.constant.CommonConstant;
import com.iflytek.itsc.auth.service.util.JsonUtil;
import com.iflytek.itsc.auth.service.util.JWTUtil;
import com.iflytek.itsc.auth.service.common.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @Classname AuthenticationRegistrationBean
 * @Description 认证过滤器
 * @Date 2023/12/18 9:42
 * @Created by wxqiu
 */
@Component
@Slf4j
public class AuthenticationRegistrationBean extends FilterRegistrationBean<Filter> {


    private PathMatcher pathMatcher;
    private static final String LOGIN_MODE_PATH = "/loginMode";

    @Value("${inner.auth.excludeUrls}")
    private String excludeUrls;

    @Autowired
    private ResourceUserService resourceUserService;


    @Value("${authentication.type}")
    private String authenticationType;

    @Autowired
    private CacheStoreService cacheStoreService;


    @PostConstruct
    public void init() {
        pathMatcher = new AntPathMatcher();
        setOrder(1);
        setFilter(new AuthenticationFilter());
    }


    class AuthenticationFilter implements Filter {

        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            //判断这个token 是不是在这个黑名单里
            final HttpServletRequest request = (HttpServletRequest) servletRequest;
            final HttpServletResponse response = (HttpServletResponse) servletResponse;
            try {
                //判断是否需要走AuthenticationFilter过滤器
                if (shouldAllowUrlAccess(request.getServletPath(), excludeUrls)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                AuthenticationHandler authenticationHandler = AuthenticationTypeFactory.getAuthenticationHandlerImpl(authenticationType);
                String token = request.getHeader(Constant.Token.TOKEN);
                if (StringUtils.isBlank(token)) {
                    log.info("token is null");
                    setRet(response, authenticationHandler.buildLoginRestResponse());
                    return;
                }
                //判断当前登录token有没有被注销
                if (isLogout(token)) {
                    setRet(response, authenticationHandler.buildLoginRestResponse());
                    return;
                }
                //不在的话 判断token 有没有失效
                if (!JWTUtil.verify(token)) {
                    setRet(response, ErrorCodeEnum.TOKEN_EXCEED_TIME);
                    return;
                }
                //校验密码是否已经被修改
                if (!authenticationHandler.checkPasswordModified(request)) {
                    setRet(response, authenticationHandler.buildLoginRestResponse());
                    return;
                }
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {
                log.error("catch unExpect exception", e);
                setRet(response, ErrorCodeEnum.UN_EXPECT_EXCEPTION);
            }

        }

        private Boolean isLogout(String token) {
            try {
                if (cacheStoreService.getCacheObject(CommonConstant.CachePrefix.LOGOUT_TOKEN+ token) != null) {
                    //在黑名单中 token 已经被注销
                    log.info("token logout");
                    return true;
                }
                return false;
            } catch (Exception e) {
                //应对redis 异常 阻塞主流程的场景
                log.error("isLogout:{}", e.getMessage(), e);
                return false;
            }
        }

        private void setRet(HttpServletResponse response, ErrorCodeEnum errorCode) throws IOException {
            RestResponse restResponse = RestResponse.buildError(errorCode);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JsonUtil.object2Json(restResponse));
        }

        private void setRet(HttpServletResponse response, RestResponse restResponse) throws IOException {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JsonUtil.object2Json(restResponse));
        }

        private Boolean shouldAllowUrlAccess(String url, String urls) {
            String[] urlsArray = mergePublicUrls(urls);
            // 不需要拦截
            if (ArrayUtils.isNotEmpty(urlsArray)) {
                for (String item : urlsArray) {
                    if (pathMatcher.match(item, url)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private String[] mergePublicUrls(String urls) {
            Set<String> publicUrls = new LinkedHashSet<>();
            if (StringUtils.isNotBlank(urls)) {
                for (String item : urls.split(",")) {
                    if (StringUtils.isNotBlank(item)) {
                        publicUrls.add(item.trim());
                    }
                }
            }
            publicUrls.add(LOGIN_MODE_PATH);
            return publicUrls.toArray(new String[0]);
        }
    }
}
