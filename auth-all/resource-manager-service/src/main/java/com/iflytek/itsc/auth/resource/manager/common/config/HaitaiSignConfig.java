package com.iflytek.itsc.auth.resource.manager.common.config;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname HaitaiSignConfig
 * @Description 海泰加密相关配置
 * @Date 2024/12/4 9:39
 * @Created by wxqiu
 */
@Configuration
@Getter
@ConditionalOnProperty(name = "security.select",havingValue = Constant.HAITAI_SECURITY_TYPE, matchIfMissing = false)
public class HaitaiSignConfig {

    /**
     * 数据唯一密钥，可以不传，不传海泰平台会自己生成，会使得同一个字符多次加密后的字符串不一致，获取的话可以通过postman调用海泰的密钥生成接口获取
     */
    @Value("${sign.haitai.keyId}")
    private String keyId;
    /**
     * 应用code,需要海泰管理员创建发放
     */
    @Value("${sign.haitai.appCode:9549e2d10c3b406bb5f6ac7bd07d2fba}")
    private String appCode;
    /**
     * 海泰密码服务平台地址
     */
    @Value("${sign.haitai.url:http://121.36.108.223:18849}")
    private String haitaiSignUrl;

    @Value("${sign.haitai.integralityUrl:http://121.36.108.223:8083}")
    private String haitaiIntegralityUrl;



}
