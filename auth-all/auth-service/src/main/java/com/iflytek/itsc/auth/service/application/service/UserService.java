package com.iflytek.itsc.auth.service.application.service;

import com.iflytek.itsc.auth.service.domain.dto.*;
import com.iflytek.itsc.auth.service.common.response.RestResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Classname AuthService
 * @Description 用户接口
 * @Date 2023/12/1 17:36
 * @Created by wxqiu
 */
public interface UserService {


    RestResponse login(LoginRequest loginRequest);

    RestResponse<LoginModeConfigResponse> getLoginModeConfig();

    RestResponse logout(HttpServletRequest request);

    RestResponse imageCaptcha(String type);

    RestResponse<TokenInfo> refreshToken(RefreshTokenRequest refreshTokenRequest);

    RestResponse getUserBaseInfo();

    RestResponse findMenu(String deployMode);

    RestResponse findWorkSpace();

    RestResponse<List<DataGroup>> findDataGroups(Long workSpaceId);

    RestResponse findApplications(Long workSpaceId, List<String> groups);

    RestResponse getLoginInfoByTicket(GetLoginInfoRequest loginInfoRequest);

    RestResponse<String> getDataGroupName(Long workSpaceId, String dataGroupToken);




}
