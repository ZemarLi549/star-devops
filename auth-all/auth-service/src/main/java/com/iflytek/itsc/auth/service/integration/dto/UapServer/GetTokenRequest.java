package com.iflytek.itsc.auth.service.integration.dto.UapServer;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname GetTokenRequest
 * @Description 获取uap token信息入参
 * @Date 2024/1/23 10:08
 * @Created by wxqiu
 */
@Data
@Builder
public class GetTokenRequest {
    private String appCode;

    private String appAuthCode;

    private String tenantId;
}
