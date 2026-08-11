package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Data;

/**
 * @Classname GetLoginInfoRequest
 * @Description 根据ticket 获取用户登录信息 入参
 * @Date 2024/1/19 16:08
 * @Created by wxqiu
 */
@Data
public class GetLoginInfoRequest {

    private String ticket;

    private String serverUrl;

    private String clientUrl;
}
