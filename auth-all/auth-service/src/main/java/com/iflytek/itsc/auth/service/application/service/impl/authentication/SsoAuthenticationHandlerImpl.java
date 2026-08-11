package com.iflytek.itsc.auth.service.application.service.impl.authentication;

import com.iflytek.itsc.auth.service.integration.SsoServerService;
import com.iflytek.itsc.auth.service.application.service.AuthenticationHandler;
import com.iflytek.itsc.auth.service.application.service.AuthenticationTypeFactory;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import com.iflytek.itsc.auth.service.common.constants.Constant;
import com.iflytek.itsc.auth.service.domain.dto.GetLoginInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname SSOAuthenticationHandlerImpl
 * @Description 集团预控登录认证相关HandlerImpl
 * @Date 2023/12/29 14:34
 * @Created by wxqiu
 */
@Component
@Slf4j
public class SsoAuthenticationHandlerImpl implements AuthenticationHandler {

    @Autowired
    private SsoServerService ssoServerService;


    @Override
    public void afterPropertiesSet() {
        AuthenticationTypeFactory.register(Constant.AuthenticationType.SSO, this);
    }

    @Override
    public boolean validateTicket(GetLoginInfoRequest loginInfoRequest, InsertUserInfo insertUserInfo) {
        return ssoServerService.validateTicket(loginInfoRequest, insertUserInfo);
    }

    @Override
    public Boolean checkPasswordModified(HttpServletRequest request) {
        return true;
    }

    @Override
    public void additionalUserInfo(InsertUserInfo insertUserInfo) {
        //nothing to do
    }
}
