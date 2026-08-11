package com.iflytek.itsc.auth.service.application.service.impl.authentication;

import com.iflytek.itsc.auth.service.application.service.AuthenticationHandler;
import com.iflytek.itsc.auth.service.application.service.AuthenticationTypeFactory;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import com.iflytek.itsc.auth.service.common.constants.Constant;
import com.iflytek.itsc.auth.service.domain.dto.GetLoginInfoRequest;
import com.iflytek.itsc.auth.service.service.ResourceUserService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUser;
import com.iflytek.itsc.auth.service.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @Classname InnerAuthenticationHandlerImpl
 * @Description 内建登录认证相关HandlerImpl
 * @Date 2023/12/29 10:45
 * @Created by wxqiu
 */
@Component
@Slf4j
public class InnerAuthenticationHandlerImpl implements AuthenticationHandler {

    @Autowired
    private ResourceUserService resourceUserService;

    @Override
    public boolean validateTicket(GetLoginInfoRequest loginInfoRequest, InsertUserInfo insertUserInfo) {
        return true;
    }

    @Override
    public Boolean checkPasswordModified(HttpServletRequest request) {
        //需要从token 里获取 userAccount
        String account = JWTUtil.getAccount(request.getHeader(Constant.Token.TOKEN));
        // 校验签发时间是否小于密码修改时间
        Optional<ResourceUser> resourceUserOptional = resourceUserService.getResourceUserBaseInfoByAccount(account);
        if (!resourceUserOptional.isPresent()) {
            //是需要重定向到登录页面的
            log.error("user not found:{}",account);
            return false;
        }
        long signTime = JWTUtil.getIat(request.getHeader(Constant.Token.TOKEN));
        if (signTime * 1000 < resourceUserOptional.get().getBaseInfo().getPasswdModifyTime()) {
            //是需要重定向到登录页面的
            log.error("password modified,account:{}",account);
            return false;

        }
        return true;
    }

    @Override
    public void additionalUserInfo(InsertUserInfo insertUserInfo) {
        //nothing to do
    }



    @Override
    public void afterPropertiesSet() throws Exception {
        AuthenticationTypeFactory.register(Constant.AuthenticationType.INNER, this);
    }
}
