package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Classname DefaultSM3HMACUtil
 * @Description 默认完整性工具类实现
 * @Date 2024/12/3 14:47
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "security.select", havingValue = Constant.DEFAULT_SECURITY_TYPE, matchIfMissing = true)
public class DefaultSM3HMACUtil extends SM3HMACUtil {

    @Override
    public String integralityEncode(String encryptContent) {
        log.debug("Using default SM3HMAC implementation for encoding");
        // 简单实现，不进行实际的HMAC计算
        return "default_hmac_value";
    }

    @Override
    public boolean verifyIntegrality(String content, String sign) {
        log.debug("Using default SM3HMAC implementation for verification");
        // 简单实现，总是返回true
        return true;
    }
}