package com.iflytek.itsc.auth.service.adapter.controller;

import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserBaseInfo;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserMenuDetail;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserWorkSpace;
import com.iflytek.itsc.auth.service.application.service.UserService;
import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.service.domain.dto.*;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.service.common.response.RestResponse;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * @Classname AuthController
 * @Description 认证授权Controller - 支持原有接口并扩展与前端代码完全兼容的新接口
 * @Date 2023/12/1 17:36
 * @Created by wxqiu
 */
@RestController
public class AuthController {

    @Autowired
    UserService userService;

    // 原有接口保持不变
    @PostMapping("/authentication/authorization")
    public RestResponse auth() {
        BaseUserInfo baseUserInfo = new BaseUserInfo();
        baseUserInfo.setAccount(UserInfoContext.getAccount());
        baseUserInfo.setUserId(String.valueOf(UserInfoContext.getUserId()));
        return RestResponse.buildSuccess(baseUserInfo);
    }

    @PostMapping("/authentication/authorization/applications")
    public RestResponse findApplications(@RequestBody @Validated ApplicationRequest applicationRequest) {
        return userService.findApplications(applicationRequest.getWorkspaceId(), applicationRequest.getGroups());
    }

    @PostMapping("/authentication/authorization/dataGroups")
    public RestResponse<List<DataGroup>> findDataGroups(@RequestBody @Validated ApplicationRequest applicationRequest) {
        return userService.findDataGroups(applicationRequest.getWorkspaceId());
    }

    @PostMapping("/authentication/authorization/dataGroupName")
    public RestResponse<String> getDataGroupName(@RequestBody @Validated ApplicationRequest applicationRequest) {
        return userService.getDataGroupName(applicationRequest.getWorkspaceId(), applicationRequest.getDataGroupToken());
    }

    @Data
    class BaseUserInfo {
        private String account;

        private String userId;
    }

    // 新增与前端代码完全兼容的接口
    @RequestMapping("/api/auth")
    @RestController
    public class ApiAuthController {

        @Autowired
        private UserService userService;

        /**
         * 登录接口 - 与前端代码完全兼容
         */
        @PostMapping("/login")
        public RestResponse<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
            // 直接调用现有服务方法，已包含用户验证和token生成逻辑
            return userService.login(loginRequest);
        }

        /**
         * 刷新token接口
         */
        @PostMapping("/refreshToken")
        public RestResponse<TokenInfo> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
            // 验证刷新令牌
            if (refreshTokenRequest.getRefreshToken() == null || refreshTokenRequest.getRefreshToken().isEmpty()) {
                throw new AuthBizException(ErrorCodeEnum.REFRESH_TOKEN_EXCEED_TIME);
            }
            return userService.refreshToken(refreshTokenRequest);
        }

        /**
         * 获取用户信息接口
         */
        @GetMapping("/userInfo")
        public RestResponse<ResourceUserBaseInfo> getUserInfo() {
            // 调用现有的获取用户基础信息服务
            return userService.getUserBaseInfo();
        }

        /**
         * 获取工作空间列表接口
         */
        @GetMapping("/workspaceList")
        public RestResponse<List<ResourceUserWorkSpace>> getWorkspaceList() {
            // 调用现有的获取工作空间服务
            return userService.findWorkSpace();
        }

        /**
         * 获取菜单信息接口
         */
        @GetMapping("/menus")
        public RestResponse<List<ResourceUserMenuDetail>> getMenus() {
            // 调用现有的获取菜单服务
            return userService.findMenu("all");
        }

        /**
         * 登出接口
         */
        @PostMapping("/logout")
        public RestResponse logout(HttpServletRequest request) {
            return userService.logout(request);
        }

        /**
         * 健康检查接口
         */
        @GetMapping("/health")
        public RestResponse<String> healthCheck() {
            return RestResponse.buildSuccess("OK");
        }
    }
}
