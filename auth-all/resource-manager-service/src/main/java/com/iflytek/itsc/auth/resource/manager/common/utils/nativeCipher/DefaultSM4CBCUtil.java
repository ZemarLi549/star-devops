package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 默认的SM4CBCUtil实现类，用于替代讯飞内部的三未信安SDK
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "security.select", havingValue = Constant.DEFAULT_SECURITY_TYPE, matchIfMissing = true)
public class DefaultSM4CBCUtil extends SM4CBCUtil {

    @Override
    public String encode(String cleartext) {
        // 简单实现，实际应用中应该使用真实的加密算法
        log.info("使用默认安全类型，不对数据进行加密");
        return cleartext;
    }

    @Override
    public String decode(String encryptStr) {
        // 简单实现，实际应用中应该使用真实的解密算法
        log.info("使用默认安全类型，不对数据进行解密");
        return encryptStr;
    }
}