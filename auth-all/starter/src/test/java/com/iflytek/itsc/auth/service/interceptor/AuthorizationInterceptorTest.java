package com.iflytek.itsc.auth.service.interceptor;

import com.iflytek.itsc.auth.service.common.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @Classname AuthorizationInterceptorTest
 * @Description 授权过滤器测试类
 * @Date 2024/1/3 17:01
 * @Created by wxqiu
 */
@SpringBootTest
@AutoConfigureMockMvc
public class AuthorizationInterceptorTest {

    private MockHttpServletResponse response;

    private MockHttpServletRequest request;

    @Autowired
    WebConfig webConfig;

    @BeforeEach
    public void setUp() {
//        response = new MockHttpServletResponse();
//        request = new MockHttpServletRequest();
//        SSOUser user = new SSOUser();
//        user.setAccountName("yzhuo");
//        user.setName("霍源治");
//        request.getSession().setAttribute(SSOConstants.SSO_USER, user);
    }

    @Test
    public void testAuthorizationInterceptor() throws ServletException, IOException {
//        BeanFactory factory = WebApplicationContextUtils
//                .getRequiredWebApplicationContext(webConfig.getServletContext());
//        AuthorizationInterceptor authorizationInterceptor = (AuthorizationInterceptor) factory.getBean("authorizationInterceptor");
//
//        authorizationInterceptor.preHandle(request,response, new Object());


    }
}
