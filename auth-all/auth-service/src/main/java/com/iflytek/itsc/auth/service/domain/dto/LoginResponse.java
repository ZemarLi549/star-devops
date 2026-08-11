package com.iflytek.itsc.auth.service.domain.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname LoginResponse
 * @Description 登录响应类
 * @Date 2023/12/13 11:26
 * @Created by wxqiu
 */

@Data
@Builder
public class LoginResponse {

    private String userId;

    private Boolean firstLogin;

   private TokenInfo tokenInfo;

   private String uapUserId;
}
