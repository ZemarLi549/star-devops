package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Classname refreshTokenRequest
 * @Description 刷新tokenInfo 入参
 * @Date 2023/12/26 10:29
 * @Created by wxqiu
 */
@Data
public class RefreshTokenRequest {


    @NotBlank(message = "刷新token不能为空")
    private String refreshToken;
}
