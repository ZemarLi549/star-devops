package com.iflytek.itsc.auth.service.interceptor;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUser;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserApi;
import com.iflytek.itsc.auth.service.common.constants.Constant;
import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import com.iflytek.itsc.auth.service.service.ResourceUserService;
import com.iflytek.itsc.auth.service.util.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Classname AuthorizationInterceptor
 * @Description 授权过滤器
 * @Date 2023/12/18 14:24
 * @Created by wxqiu
 */
@Component
@Slf4j
public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String LOGIN_MODE_PATH = "/loginMode";
    private static final String AUTH_LOGIN_MODE_PATH = "/auth/loginMode";

    @Autowired
    private ResourceUserService resourceUserService;

    private static final String ONE_STAR = "/*";
    private static final String TWO_STAR = "/**";

    @Value("${inner.apiPermission.excludeUrls}")
    private String excludeUrls;

    private PathMatcher pathMatcher = new AntPathMatcher();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isPublicRequest(request)) {
            return true;
        }
        UserInfoContext.LoginUser user = new UserInfoContext.LoginUser();
        //获取当前登录用户名
        String token = request.getHeader(Constant.Token.TOKEN);
        String account = JWTUtil.getAccount(token);
        user.setAccount(account);
        String userId = JWTUtil.getUserId(token);
        // 添加null检查，避免空指针异常
        user.setUserId(userId != null ? Long.valueOf(userId) : null);
        //获取该请求选中的工作空间id
        String workspaceIdSelected = request.getHeader(Constant.WORKSPACE_ID);
        user.setWorkspaceId(StringUtils.isBlank(workspaceIdSelected) ? null : Long.valueOf(workspaceIdSelected));
        Optional<ResourceUser> resourceUserOptional = resourceUserService.getResourceUserBaseInfoByAccount(account);
        //校验用户是否存在
        if (!resourceUserOptional.isPresent()) {
            throw new AuthBizException(ErrorCodeEnum.USER_NOT_FOUND);
        }
        //鉴权
        checkApiPermission(request, resourceUserOptional.get().getMenuResourceApiMap(), resourceUserOptional.get().isSuperAdmin(), workspaceIdSelected);
        user.setSuperAdmin(resourceUserOptional.get().isSuperAdmin());
        user.setRelateWorkSpaceIdSet(resourceUserOptional.get().getRelateWorkSpaceIdSet());
        UserInfoContext.setUser(user);
        return true;
    }

    private boolean isPublicRequest(HttpServletRequest request) {
        String servletPath = StringUtils.defaultString(request.getServletPath());
        String requestUri = StringUtils.defaultString(request.getRequestURI());
        String requestUrl = StringUtils.defaultString(request.getRequestURL() == null ? null : request.getRequestURL().toString());
        return servletPath.contains("loginMode")
                || requestUri.contains("loginMode")
                || requestUrl.contains("loginMode")
                || LOGIN_MODE_PATH.equals(servletPath)
                || AUTH_LOGIN_MODE_PATH.equals(servletPath)
                || LOGIN_MODE_PATH.equals(requestUri)
                || AUTH_LOGIN_MODE_PATH.equals(requestUri);
    }

    private void checkApiPermission(HttpServletRequest request, Map<Long, List<ResourceUserApi>> menuResourceApiMap, boolean isSuperAdmin, String workspaceIdSelected) {
        //超管拥有所有权限
        if (isSuperAdmin) {
            return;
        }
        String path = buildFinalPath(request);
        String method = path.split(",")[0];
        String apiPath = path.split(",")[1];
        // 不做api 的鉴权/auth/workspace,/auth/guideState,/auth/userBaseInfo,/auth/updateUser,/auth/updatePassword
        if (shouldAllowUrlAccess(apiPath, excludeUrls)) {
            return;
        }
        if (StringUtils.isBlank(workspaceIdSelected)) {
            log.error("没有选中的workspaceId,path:{}", path);
            throw new AuthBizException(ErrorCodeEnum.API_FORBIDDEN);
        }
        if (menuResourceApiMap == null) {
            log.error("没有相关的资源权限");
            throw new AuthBizException(ErrorCodeEnum.API_FORBIDDEN);
        }
        List<ResourceUserApi> resourceUserApis = menuResourceApiMap.get(Long.valueOf(workspaceIdSelected));
        if (resourceUserApis == null) {
            log.error("在空间ID：{}下没有相关的资源权限", workspaceIdSelected);
            throw new AuthBizException(ErrorCodeEnum.API_FORBIDDEN);
        }
        for (ResourceUserApi resourceUserApi : resourceUserApis) {
            String configuredMethod = resourceUserApi.getMethod();
            String configuredApiPath = resourceUserApi.getPath();
            boolean isMatch = matchPath(apiPath, configuredApiPath);
            // 如果method为空，则包含匹配
            if (StringUtils.isEmpty(configuredMethod) && isMatch) {
                return;
            }
            if (StringUtils.isNotEmpty(configuredMethod) && method.equals(configuredMethod) && isMatch) {
                return;
            }

        }
        // 如果没有匹配到，抛出异常
        log.error("在空间ID：{}下没有匹配到任何路径，path:{}", workspaceIdSelected, path);
        throw new AuthBizException(ErrorCodeEnum.API_FORBIDDEN);
    }

    /**
     * @param apiPath        子平台传过来的路径
     * @param configuredPath 系统配置的路径
     * @return
     */
    private boolean matchPath(String apiPath, String configuredPath) {
        // 检查configuredPath中是否包含/**
        if (configuredPath.contains(TWO_STAR)) {
            // 获取/**之前的部分
            String beforeStar = configuredPath.substring(0, configuredPath.indexOf(TWO_STAR));
            // 检查apiPath是否以beforeStar开头
            return apiPath.startsWith(beforeStar);
        } else if (configuredPath.contains(ONE_STAR)) {
            String[] configuredPathArray = configuredPath.split("/");
            String[] apiPathArray = apiPath.split("/");
            if (configuredPathArray.length != apiPathArray.length) {
                return false;
            }
            for (int i = 0; i < configuredPathArray.length; i++) {
                if (configuredPathArray[i].equals("*")) {
                    continue;
                }
                if (!configuredPathArray[i].equals(apiPathArray[i])) {
                    return false;
                }
            }
            return true;

        }
        // 如果configuredPath中没有/**或/*，则直接比较两个字符串是否相等
        return configuredPath.equals(apiPath);
    }

    private String buildFinalPath(HttpServletRequest request) {
        //获取该请求的url
        String path = request.getHeader(Constant.PATH);
        //path参数的合法性校验
        if (StringUtils.isNotEmpty(path) && !path.contains(",")) {
            log.error("path参数不合法,path={}", path);
            throw new AuthBizException(ErrorCodeEnum.API_FORBIDDEN);
        }
        if (StringUtils.isEmpty(path)) {
            path = request.getMethod() + "," + request.getContextPath() + request.getServletPath();
        }
        return path;
    }

    private Boolean shouldAllowUrlAccess(String url, String urls) {
        String[] urlsArray = urls.split(",");
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

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse
            response, @NotNull Object handler, Exception ex) {
        // 请求完成后清理ThreadLocal数据，防止内存泄漏
        UserInfoContext.remove();
    }
}
