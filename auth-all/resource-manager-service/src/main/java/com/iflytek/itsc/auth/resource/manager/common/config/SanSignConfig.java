package com.iflytek.itsc.auth.resource.manager.common.config;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname SanSignConfig
 * @Description
 * @Date 2024/5/8 14:07
 * @Created by wxqiu
 */
@Configuration
@Getter
@ConditionalOnProperty(name = "security.select",havingValue = Constant.SAN_SECURITY_TYPE, matchIfMissing = false)
public class SanSignConfig {

    @Value("${sign.san.key.id:SM4_test}")
    private String sanKeyId;

    @Value("${sign.san.app.id:app1}")
    private String sanAppId;

    @Value("${sign.san.app.pws:Aa123456}")
    private String sanAppPws;

    @Value("${sign.san.config.path:D:/auth-manager/resource-manager/resource-manager-service/src/main/resources/swsds.ini}")
    private String sanConfigPath;

    // 移除了对讯飞内部SDK的依赖，因为相关的加密工具类已经被简化
}
