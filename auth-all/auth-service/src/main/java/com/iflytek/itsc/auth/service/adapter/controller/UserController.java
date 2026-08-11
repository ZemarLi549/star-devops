package com.iflytek.itsc.auth.service.adapter.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceSpaceApplication;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserBaseInfo;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserMenuDetail;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserWorkSpace;
import com.iflytek.itsc.auth.service.application.service.UserService;
import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.service.domain.dto.*;
import com.iflytek.itsc.auth.service.integration.ResourceManagerService;
import com.iflytek.itsc.auth.service.common.response.RestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Classname UserController
 * @Description 用户相关操作Controller
 * @Date 2023/12/13 17:34
 * @Created by wxqiu
 */
@RestController
public class UserController {

    @Autowired
    UserService userService;
    @Value("${deploy_mode:all}")
    private String deployMode;

    @Autowired
    ResourceManagerService resourceManagerService;

    @PostMapping("/login")
    public RestResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return userService.login(loginRequest);
    }

    @GetMapping("/loginMode")
    public RestResponse<LoginModeConfigResponse> getLoginMode() {
        return userService.getLoginModeConfig();
    }

    @PostMapping("/logout")
    public RestResponse logout(HttpServletRequest request) {
        return userService.logout(request);

    }

    /**
     * 获取图片验证码
     */
    @GetMapping(value = "/captcha")
    public RestResponse imageCaptcha(@RequestParam(value = "type", required = false) String type) {
        if (StringUtils.isBlank(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        return userService.imageCaptcha(type);
    }

    /**
     * 刷新token
     */
    @PostMapping("/refreshToken")
    public RestResponse<TokenInfo> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return userService.refreshToken(refreshTokenRequest);
    }

    /**
     * 获取当前登录用户的工作空间相关信息
     */
    @GetMapping("/workspace")
    public RestResponse<List<ResourceUserWorkSpace>> getWorkSpace() {
        return userService.findWorkSpace();
    }

    /**
     * 获取当前登录用户的工作空年间下的数据单元列表
     */
    @GetMapping("/dataGroups")
    public RestResponse<List<DataGroup>> findDataGroups() {
        Long workSpacesId = UserInfoContext.getWorkspaceId();
        return userService.findDataGroups(workSpacesId);
    }

    @GetMapping("/applications")
    public RestResponse<List<ResourceSpaceApplication>> findApplications() {
        Long workSpacesId = UserInfoContext.getWorkspaceId();
        // group 当前版本传null 头部还没有做 数据单元下列表
        return userService.findApplications(workSpacesId, null);
    }

    /**
     * 获取当前登录用户的用户基础信息
     */
    @GetMapping("/userBaseInfo")
    public RestResponse<ResourceUserBaseInfo> getUserBaseInfo() {
        return userService.getUserBaseInfo();
    }

    /**
     * 获取当前登录用户的菜单信息
     */
    @GetMapping("/menu")
    public RestResponse<List<ResourceUserMenuDetail>> getMenu() {
        return userService.findMenu(deployMode);
    }

    /**
     * 更新用户
     */
    @PostMapping("/updateUser")
    public RestResponse updateUser(@RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        validLoginUserId(userUpdateRequest.getUserId());
        resourceManagerService.updateUser(userUpdateRequest);
        return RestResponse.buildSuccess();
    }

    @PostMapping("/updatePassword")
    public RestResponse updatePassword(@RequestBody @Validated PasswordUpdateRequest passwordUpdateRequest) {
        validLoginUserId(passwordUpdateRequest.getUserId());
        resourceManagerService.updatePassword(passwordUpdateRequest);
        return RestResponse.buildSuccess();
    }

    private void validLoginUserId(String userId) {
        Long userId1 = UserInfoContext.getUserId();
        if (!userId.equals(String.valueOf(userId1))) {
            throw new AuthBizException(ErrorCodeEnum.UPDATE_FORBIDDEN);
        }
    }

    @PostMapping("/loginInfo")
    public RestResponse getLoginInfoByTicket(@RequestBody @Valid GetLoginInfoRequest loginInfoRequest) {
        return userService.getLoginInfoByTicket(loginInfoRequest);
    }

    @PostMapping("/guideState")
    public RestResponse UpdateGuideState(@RequestBody @Valid UpdateGuideStateRequest updateGuideStateRequest) {
        resourceManagerService.updateGuideState(updateGuideStateRequest);
        return RestResponse.buildSuccess();
    }

    @GetMapping("/validateIntegrality")
    public RestResponse validateIntegrality() {
        resourceManagerService.validateIntegrality();
        return RestResponse.buildSuccess();
    }

    @PostMapping("/initCipherData")
    public RestResponse initCipherData() {
        resourceManagerService.initCipherData();
        return RestResponse.buildSuccess();
    }


}
