package com.iflytek.itsc.auth.service.application.service;

import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.service.domain.dto.GetLoginInfoRequest;
import com.iflytek.itsc.auth.service.common.response.RestResponse;
import org.springframework.beans.factory.InitializingBean;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname AuthenticationHandler
 * @Description 认证Handler
 * @Date 2023/12/29 10:43
 * @Created by wxqiu
 */
public interface AuthenticationHandler extends InitializingBean {
    boolean validateTicket(GetLoginInfoRequest loginInfoRequest, InsertUserInfo insertUserInfo);

    Boolean checkPasswordModified(HttpServletRequest request);

    void additionalUserInfo(InsertUserInfo insertUserInfo);

    default RestResponse buildLoginRestResponse() {
       return  RestResponse.buildError(ErrorCodeEnum.NEED_LOGIN);
    }


}
