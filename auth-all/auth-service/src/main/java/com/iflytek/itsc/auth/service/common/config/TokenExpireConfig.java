package com.iflytek.itsc.auth.service.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname TokenExpireConfig
 * @Description Token 过期时间配置
 * @Date 2024/1/18 11:21
 * @Created by wxqiu
 */
@Configuration
@ConfigurationProperties("token.expire")
@Data
public class TokenExpireConfig {

    private int accessTokenExpire;

    private int refreshTokenExpire;
}
