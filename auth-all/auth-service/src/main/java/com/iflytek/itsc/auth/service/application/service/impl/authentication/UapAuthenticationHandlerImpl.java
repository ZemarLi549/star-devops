package com.iflytek.itsc.auth.service.application.service.impl.authentication;

import com.iflytek.itsc.auth.service.integration.UapServerService;
import com.iflytek.itsc.auth.service.application.service.AuthenticationHandler;
import com.iflytek.itsc.auth.service.application.service.AuthenticationTypeFactory;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import com.iflytek.itsc.auth.service.integration.dto.UapServer.UapUserInfo;
import com.iflytek.itsc.auth.service.common.constants.Constant;
import com.iflytek.itsc.auth.service.domain.dto.GetLoginInfoRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname UapAuthenticationHandlerImpl
 * @Description uap登录相关HandlerImpl
 * @Date 2023/12/29 13:44
 * @Created by wxqiu
 */
@Component
@Slf4j
public class UapAuthenticationHandlerImpl implements AuthenticationHandler {

    @Autowired
    private UapServerService uapServerService;


    @Override
    public boolean validateTicket(GetLoginInfoRequest loginInfoRequest, InsertUserInfo insertUserInfo) {
        return uapServerService.validateTicket(loginInfoRequest, insertUserInfo);
    }

    @Override
    public Boolean checkPasswordModified(HttpServletRequest request) {
        return true;
    }

    @Override
    public void additionalUserInfo(InsertUserInfo insertUserInfo) {
        UapUserInfo uapUserInfo = uapServerService.getUserInfo(insertUserInfo.getAccount(),insertUserInfo.getTenantId());
        if (uapUserInfo != null) {
            insertUserInfo.setNickName(StringUtils.isBlank(uapUserInfo.getName()) ? insertUserInfo.getAccount() : uapUserInfo.getName());
            insertUserInfo.setPhone(uapUserInfo.getPhone());
            insertUserInfo.setEmail(uapUserInfo.getEmail());
        }else{
            insertUserInfo.setNickName(insertUserInfo.getAccount());
        }
    }

    @Override
    public void afterPropertiesSet() {
        AuthenticationTypeFactory.register(Constant.AuthenticationType.UAP, this);

    }
}
