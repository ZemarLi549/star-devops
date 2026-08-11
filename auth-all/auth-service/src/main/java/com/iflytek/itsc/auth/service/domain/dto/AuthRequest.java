package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Classname AuthRequest
 * @Description 认证授权请求入参
 * @Date 2023/12/13 17:44
 * @Created by wxqiu
 */
@Data
public class AuthRequest {

    @NotBlank
    private String path;
}
