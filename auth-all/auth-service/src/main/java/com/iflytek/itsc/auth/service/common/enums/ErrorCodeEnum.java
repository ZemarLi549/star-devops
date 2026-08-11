package com.iflytek.itsc.auth.service.common.enums;

import com.iflytek.itsc.auth.common.enums.BaseErrorCodeEnum;

/**
 * @Classname ErrorCodeEnum
 * @Description 错诶枚举
 * @Date 2023/12/4 15:12
 * @Created by wxqiu
 */
public enum ErrorCodeEnum implements BaseErrorCodeEnum {

    USER_NOT_FOUND("USER_NOT_FOUND", "用户不存在"),

    USER_IS_DISABLED("USER_IS_DISABLED", "用户被禁用"),
    USER_PASSWORD_ERROR("USER_PASSWORD_ERROR", "用户名或者密码错误"),

    CAPTCHA_ERROR("CAPTCHA_ERROR", "验证码错误"),

    TOKEN_EXCEED_TIME("TOKEN_EXCEED_TIME", "token已失效"),

    REFRESH_TOKEN_EXCEED_TIME("REFRESH_TOKEN_EXCEED_TIME", "您离开的太久了，请重新登录~"),

    MENU_NOT_FOUND("MENU_NOT_FOUND", "菜单不存在"),

    WORKSPACE_NOT_FOUND("WORKSPACE_NOT_FOUND", "工作空间不存在"),

    API_FORBIDDEN("API_FORBIDDEN", "没有访问该接口的权限"),

    INTEGRATION_ERROR("INTEGRATION_ERROR", "外调接口错误"),

    LOGIN_API_UNSUPPORTED("LOGIN_API_UNSUPPORTED", "当前不是自建用户登录模式，登录接口不可用"),

    LOGIN_MODE_NOT_ALLOWED("LOGIN_MODE_NOT_ALLOWED", "当前登录模式不支持所选认证方式"),

    LDAP_CONFIG_INVALID("LDAP_CONFIG_INVALID", "LDAP 配置不完整"),

    LDAP_AUTH_FAILED("LDAP_AUTH_FAILED", "LDAP 认证失败"),

    NEED_LOGIN("NEED_LOGIN","需要重新登录"),

    UN_EXPECT_EXCEPTION("UN_EXPECT_EXCEPTION","未知异常"),

    DATA_GROUP_NOT_FOUND("DATA_GROUP_NOT_FOUND", "数据单元不存在"),

    APPLICATION_NOT_FOUND("APPLICATION_NOT_FOUND", "应用不存在"),

    UPDATE_FORBIDDEN("UPDATE_FORBIDDEN", "禁止修改"),

    ;

    private final String errorCode;

    private final String errorMsg;

    ErrorCodeEnum(String errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
