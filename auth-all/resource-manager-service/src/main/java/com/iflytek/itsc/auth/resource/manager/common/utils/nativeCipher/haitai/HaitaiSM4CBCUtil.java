package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.SM4CBCUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Classname HaitaiSM4CBCUtil
 * @Description 海泰数据机密性校验工具类
 * @Date 2024/12/3 15:00
 * @Created by wxqiu
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "security.select", havingValue = Constant.HAITAI_SECURITY_TYPE, matchIfMissing = false)
public class HaitaiSM4CBCUtil extends SM4CBCUtil {

    @Override
    public String encode(String cleartext) {
        log.warn("HaitaiSM4CBCUtil encode is not implemented in this environment");
        return cleartext; // 简单返回原文，不进行实际加密
    }

    @Override
    public String decode(String encryptStr) {
        log.warn("HaitaiSM4CBCUtil decode is not implemented in this environment");
        return encryptStr; // 简单返回原文，不进行实际解密
    }
}
