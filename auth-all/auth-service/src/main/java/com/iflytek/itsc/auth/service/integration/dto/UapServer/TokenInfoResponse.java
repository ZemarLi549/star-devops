package com.iflytek.itsc.auth.service.integration.dto.UapServer;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Classname TokenInfo
 * @Description uap token信息
 * @Date 2024/1/23 10:27
 * @Created by wxqiu
 */
@Data
public class TokenInfoResponse {
    private String token;

    private Long expiresInMillis;

    private LocalDateTime expiresDateTime = LocalDateTime.now();

}
