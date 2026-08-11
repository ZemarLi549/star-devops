package com.iflytek.itsc.auth.service.domain.dto;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Classname LoginRequest
 * @Description 登录请求入参
 * @Date 2023/12/4 9:45
 * @Created by wxqiu
 */
@Data
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    private String account;

    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 登录方式：LOCAL / LDAP
     */
    private String loginMode;

    /**
     * 验证码id
     */
    // @NotBlank(message = "验证码id不能为空")
    private String captchaId;

    private ImageCaptchaTrack captchaTrack;
}
