package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname VerifyIntegralityForm
 * @Description 海泰完整性校验入参
 * @Date 2024/12/5 10:38
 * @Created by wxqiu
 */
@Data
@Builder
public class VerifyIntegralityRequest {

    private String data;

    private String signature;
}
