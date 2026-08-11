package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.domain;

import lombok.Data;

/**
 * @Classname HaitaiResponse
 * @Description 海泰机密性响应体
 * @Date 2024/12/4 9:53
 * @Created by wxqiu
 */
@Data
public class HaitaiResponse<T> {

    private Integer code;

    private String msg;
    /**
     * 方法调用时传入的 transId，作为结果原样
     * 返回
     */
    private String transId;

    private T data;
}
