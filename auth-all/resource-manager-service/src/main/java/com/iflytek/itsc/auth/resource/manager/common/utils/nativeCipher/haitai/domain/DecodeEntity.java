package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.domain;

import lombok.Data;

/**
 * @Classname decodeEntity
 * @Description 海泰机密性解密后返回的实体
 * @Date 2024/12/4 14:42
 * @Created by wxqiu
 */
@Data
public class DecodeEntity {
    /**
     * 编码类型:十六进制编码(HEX),BASE64 编
     * 码(BASE64
     */
    private String encode;
    /**
     * 数据密钥唯一标识
     */
    private String keyId;
    /**
     * 数据明文
     */
    private String decData;
}
