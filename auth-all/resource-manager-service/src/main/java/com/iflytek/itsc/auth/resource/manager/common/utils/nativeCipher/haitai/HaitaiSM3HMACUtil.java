package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai;

import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.SM3HMACUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @Classname HaiTaiSM3HMACUtil
 * @Description 海泰数据完整性校验工具类
 * @Date 2024/12/3 14:35
 * @Created by wxqiu
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "security.select", havingValue = Constant.HAITAI_SECURITY_TYPE, matchIfMissing = false)
public class HaitaiSM3HMACUtil extends SM3HMACUtil {

    @Override
    public String integralityEncode(String encryptContent) {
        log.warn("HaitaiSM3HMACUtil integralityEncode is not implemented in this environment");
        return "dummy_hmac_value_for_testing";
    }

    @Override
    public boolean verifyIntegrality(String content, String sign) {
        log.warn("HaitaiSM3HMACUtil verifyIntegrality is not implemented in this environment");
        return true; // 简单返回true，不进行实际验证
    }
}
