package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname IntegralityRequest
 * @Description 海泰完整性入参
 * @Date 2024/12/5 10:03
 * @Created by wxqiu
 */
@Data
@Builder
public class IntegralityRequest {
    /**
     * 需要加签的数据
     */
    private String data;
}
