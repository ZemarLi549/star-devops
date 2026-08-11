package com.iflytek.itsc.auth.service.application.service.impl;

import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cn.hutool.core.collection.CollectionUtil;
import com.iflytek.itsc.auth.common.cache.CacheStoreService;
import com.iflytek.itsc.auth.common.constant.CommonConstant;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.domain.dto.*;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.service.application.service.AuthenticationHandler;
import com.iflytek.itsc.auth.service.application.service.AuthenticationTypeFactory;
import com.iflytek.itsc.auth.service.application.service.UserService;
import com.iflytek.itsc.auth.service.common.config.TokenExpireConfig;
import com.iflytek.itsc.auth.service.common.constants.Constant;
import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.service.common.enums.ViewTypeEnum;
import com.iflytek.itsc.auth.service.domain.dto.DataGroup;
import com.iflytek.itsc.auth.service.domain.dto.*;
import com.iflytek.itsc.auth.service.integration.ResourceManagerService;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import com.iflytek.itsc.auth.service.service.ResourceUserService;
import com.iflytek.itsc.auth.service.service.LdapAuthenticationService;
import com.iflytek.itsc.auth.service.util.JsonUtil;
import com.iflytek.itsc.auth.service.util.JWTUtil;
import com.iflytek.itsc.auth.service.util.HMacUtil;
import com.iflytek.itsc.auth.service.common.response.RestResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Classname UserServiceImpl
 * @Description 用户相关ServiceImpl
 * @Date 2023/12/1 17:37
 * @Created by wxqiu
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private TokenExpireConfig tokenExpire;

    @Autowired
    private ImageCaptchaApplication imageCaptchaApplication;

    @Autowired
    ResourceManagerService resourceManagerService;

    @Value("${authentication.type}")
    private String authenticationType;

    @Value("${authentication.login-mode:LOCAL}")
    private String authenticationLoginMode;

    @Autowired
    private ResourceUserService resourceUserService;

    @Autowired
    private LdapAuthenticationService ldapAuthenticationService;


    @Autowired
    private CacheStoreService cacheStoreService;

    // 验证码启用开关配置
    @Value("${captcha.enable:true}")
    private boolean captchaEnable;

    @Override
    public RestResponse<LoginResponse> login(LoginRequest loginRequest) {
        if (!Constant.AuthenticationType.INNER.equals(authenticationType)) {
            throw new AuthBizException(ErrorCodeEnum.LOGIN_API_UNSUPPORTED);
        }
        // 根据配置决定是否校验验证码
        if (captchaEnable) {
            boolean captchaCheck = imageCaptchaApplication.matching(loginRequest.getCaptchaId(), loginRequest.getCaptchaTrack());
            if (!captchaCheck) {
                throw new AuthBizException(ErrorCodeEnum.CAPTCHA_ERROR);
            }
        }
        String requestLoginMode = resolveRequestLoginMode(loginRequest.getLoginMode());
        validateConfiguredLoginMode(requestLoginMode);
        ResourceUser resourceUser = authenticateAndLoadUser(loginRequest, requestLoginMode);
        //更新用户登录时间
        resourceManagerService.updateLoginTime(resourceUser.getBaseInfo().getUserId());
        return RestResponse.buildSuccess(LoginResponse.builder().userId(resourceUser.getUserId().toString()).firstLogin(isFirstLogin(resourceUser.getBaseInfo())).
                tokenInfo(new TokenInfo(resourceUser.getUserId().toString(), resourceUser.getAccount(), tokenExpire)).build());
    }

    @Override
    public RestResponse<LoginModeConfigResponse> getLoginModeConfig() {
        String currentMode = normalizeConfiguredLoginMode();
        List<String> options = Constant.LoginMode.MIXED.equals(currentMode)
                ? Arrays.asList(Constant.LoginMode.LOCAL, Constant.LoginMode.LDAP)
                : Collections.singletonList(currentMode);
        return RestResponse.buildSuccess(LoginModeConfigResponse.builder()
                .mode(currentMode)
                .options(options)
                .captchaEnabled(captchaEnable)
                .build());
    }


    @Override
    public RestResponse logout(HttpServletRequest request) {
        String token = request.getHeader(Constant.Token.TOKEN);
        //获取token 过期时间
        Long exp = JWTUtil.getExp(token);
        String account = JWTUtil.getAccount(token);
        Long expire = exp - System.currentTimeMillis() / 1000;
        if (expire > 0) {
            cacheStoreService.setCacheObject(CommonConstant.CachePrefix.LOGOUT_TOKEN + token, account, (long) tokenExpire.getAccessTokenExpire(), TimeUnit.MINUTES);
        }
        // 删除缓存
        resourceUserService.deleteResourceUser(account);
        return RestResponse.buildSuccess();

    }

    @Override
    public RestResponse imageCaptcha(String type) {
        CaptchaResponse<ImageCaptchaVO> response = imageCaptchaApplication.generateCaptcha(type);
        return RestResponse.buildSuccess(response);
    }

    @Override
    public RestResponse<TokenInfo> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        //验证token
        if (!JWTUtil.verify(refreshTokenRequest.getRefreshToken())) {
            throw new AuthBizException(ErrorCodeEnum.REFRESH_TOKEN_EXCEED_TIME);
        }
        String userId = JWTUtil.getUserId(refreshTokenRequest.getRefreshToken());
        String account = JWTUtil.getAccount(refreshTokenRequest.getRefreshToken());
        return RestResponse.buildSuccess(new TokenInfo(userId, account, tokenExpire));
    }

    @Override
    public RestResponse findWorkSpace() {
        Long userId = UserInfoContext.getUserId();
        List<ResourceUserWorkSpace> resourceUserWorkSpaces = Optional.ofNullable(resourceManagerService.listWorkspaces(userId)).orElseThrow(() -> new AuthBizException(ErrorCodeEnum.WORKSPACE_NOT_FOUND));
        return RestResponse.buildSuccess(resourceUserWorkSpaces);
    }

    @Override
    public RestResponse<List<DataGroup>> findDataGroups(Long workSpaceId) {
        List<SysDataGroupEntity> dataGroupEntities = resourceManagerService.findDataGroups(workSpaceId);
        if (CollectionUtil.isEmpty(dataGroupEntities)) {
            return RestResponse.buildSuccess(Collections.emptyList());
        }
        List<DataGroup> dataGroups = dataGroupEntities.stream().map(dataGroupEntity -> {
            return DataGroup.builder().dataGroupToken(dataGroupEntity.getDataGroupToken()).dataGroupName(dataGroupEntity.getDataGroupName()).build();
        }).collect(Collectors.toList());
        return RestResponse.buildSuccess(dataGroups);
    }

    @Override
    public RestResponse<String> getDataGroupName(Long workSpaceId, String dataGroupToken) {
        List<SysDataGroupEntity> dataGroupEntities = resourceManagerService.findDataGroups(workSpaceId);
        String dataGroupName = null;
        if (CollectionUtil.isEmpty(dataGroupEntities)) {
            return RestResponse.buildSuccess(null);
        }
        for (SysDataGroupEntity dataGroup : dataGroupEntities) {
            if (dataGroup.getDataGroupToken().equals(dataGroupToken)) {
                dataGroupName = dataGroup.getDataGroupName();
                break;
            }
        }
        return RestResponse.buildSuccess(dataGroupName);

    }

    @Override
    public RestResponse findApplications(Long workSpaceId, List<String> groups) {
        List<ResourceSpaceApplication> resourceSpaceApplications = resourceManagerService.findApplications(workSpaceId);
        //根据groups 再过滤一遍
        if (CollectionUtil.isNotEmpty(groups) && CollectionUtil.isNotEmpty(resourceSpaceApplications)) {
            resourceSpaceApplications.stream().filter(resourceSpaceApplication1 -> groups.contains(resourceSpaceApplication1.getDataGroupToken())).collect(Collectors.toList());
        }
        return RestResponse.buildSuccess(resourceSpaceApplications);
    }


    @Override
    public RestResponse getUserBaseInfo() {
        String account = UserInfoContext.getAccount();
        ResourceUser resourceUser = resourceUserService.getResourceUserBaseInfoByAccount(account).orElseThrow(() -> new AuthBizException(ErrorCodeEnum.USER_NOT_FOUND));
        setGuideState(resourceUser.getBaseInfo());
        resourceUser.getBaseInfo().setPasswd(null);
        return RestResponse.buildSuccess(resourceUser.getBaseInfo());
    }

    private void setGuideState(ResourceUserBaseInfo resourceUserBaseInfo) {
        Map<String, Boolean> map = JsonUtil.json2Object(resourceUserBaseInfo.getGuideState(), Map.class);
        if (map == null) {
            map = new HashMap<>();
        }
        for (ViewTypeEnum viewTypeEnum : ViewTypeEnum.values()) {
            map.putIfAbsent(viewTypeEnum.getValue(), true);
        }
        resourceUserBaseInfo.setGuideState(JsonUtil.object2Json(map));
    }


    @Override
    public RestResponse findMenu(String deployMode) {
        Long workspaceId = UserInfoContext.getWorkspaceId();
        Long userId = UserInfoContext.getUserId();
        List<ResourceUserMenuDetail> resourceUserMenuDetails = resourceManagerService.listMenus(userId, workspaceId, deployMode);
        return RestResponse.buildSuccess(resourceUserMenuDetails);
    }


    @Override
    public RestResponse getLoginInfoByTicket(GetLoginInfoRequest loginInfoRequest) {
        AuthenticationHandler authenticationHandler = AuthenticationTypeFactory.getAuthenticationHandlerImpl(authenticationType);
        InsertUserInfo insertUserInfo = new InsertUserInfo();
        if (authenticationHandler.validateTicket(loginInfoRequest, insertUserInfo)) {
            Optional<ResourceUser> resourceUserOptional = resourceUserService.getResourceUserBaseInfoByAccount(insertUserInfo.getAccount());
            ResourceUser resourceUser = null;
            if (!resourceUserOptional.isPresent()) {
                authenticationHandler.additionalUserInfo(insertUserInfo);
                resourceUser = insertResourceUserBaseInfo(insertUserInfo);
            } else {
                resourceUser = resourceUserOptional.get();
            }
            // 判断用户是否被禁用
            if (!resourceUser.getBaseInfo().isIsactive()) {
                throw new AuthBizException(ErrorCodeEnum.USER_IS_DISABLED);
            }
            //更新用户登录时间
            resourceManagerService.updateLoginTime(resourceUser.getUserId());
            return RestResponse.buildSuccess(LoginResponse.builder().userId(resourceUser.getUserId().toString()).firstLogin(isFirstLogin(resourceUser.getBaseInfo())).uapUserId(insertUserInfo.getUserId()).
                    tokenInfo(new TokenInfo(resourceUser.getUserId().toString(), resourceUser.getAccount(), tokenExpire)).build());
        }
        //校验不通过 返回重新登录code
        return authenticationHandler.buildLoginRestResponse();

    }

    private ResourceUser insertResourceUserBaseInfo(InsertUserInfo insertUserInfo) {
        //插入用户信息到resourceManager
        ResourceUser user = resourceManagerService.insertUser(insertUserInfo);
        //保存到缓存
        resourceUserService.saveResourceUserToCache(user);
        return user;
    }

    /**
     * 获取用户是否第一次登录
     *
     * @param resourceUserBaseInfo
     * @return
     */
    private Boolean isFirstLogin(ResourceUserBaseInfo resourceUserBaseInfo) {
        Boolean firstLogin = false;
        //判断是否第一次登录
        if (!resourceUserBaseInfo.isIsloggedin()) {
            //是第一次登录
            firstLogin = true;
            resourceManagerService.updateLogged(resourceUserBaseInfo.getUserId());
        }
        return firstLogin;
    }

    private ResourceUser authenticateAndLoadUser(LoginRequest loginRequest, String loginMode) {
        ResourceUser resourceUser = resourceUserService.getResourceUserBaseInfoByAccount(loginRequest.getAccount())
                .orElseThrow(() -> new AuthBizException(ErrorCodeEnum.USER_NOT_FOUND));
        if (!resourceUser.getBaseInfo().isIsactive()) {
            throw new AuthBizException(ErrorCodeEnum.USER_IS_DISABLED);
        }
        if (Constant.LoginMode.LDAP.equals(loginMode)) {
            ldapAuthenticationService.authenticate(loginRequest.getAccount(), loginRequest.getPassword());
            return resourceUser;
        }
        if (!HMacUtil.hMacMd5(loginRequest.getPassword()).equals(resourceUser.getBaseInfo().getPasswd())) {
            throw new AuthBizException(ErrorCodeEnum.USER_PASSWORD_ERROR);
        }
        return resourceUser;
    }

    private void validateConfiguredLoginMode(String requestLoginMode) {
        String configuredMode = normalizeConfiguredLoginMode();
        if (Constant.LoginMode.MIXED.equals(configuredMode)) {
            return;
        }
        if (!configuredMode.equals(requestLoginMode)) {
            throw new AuthBizException(ErrorCodeEnum.LOGIN_MODE_NOT_ALLOWED);
        }
    }

    private String resolveRequestLoginMode(String requestLoginMode) {
        String configuredMode = normalizeConfiguredLoginMode();
        if (StringUtils.isBlank(requestLoginMode)) {
            return Constant.LoginMode.MIXED.equals(configuredMode) ? Constant.LoginMode.LOCAL : configuredMode;
        }
        String normalized = requestLoginMode.trim().toUpperCase(Locale.ROOT);
        if (!Arrays.asList(Constant.LoginMode.LOCAL, Constant.LoginMode.LDAP).contains(normalized)) {
            throw new AuthBizException(ErrorCodeEnum.LOGIN_MODE_NOT_ALLOWED);
        }
        return normalized;
    }

    private String normalizeConfiguredLoginMode() {
        String mode = StringUtils.defaultIfBlank(authenticationLoginMode, Constant.LoginMode.LOCAL)
                .trim()
                .toUpperCase(Locale.ROOT);
        if (!Arrays.asList(Constant.LoginMode.LOCAL, Constant.LoginMode.LDAP, Constant.LoginMode.MIXED).contains(mode)) {
            return Constant.LoginMode.LOCAL;
        }
        return mode;
    }


}
