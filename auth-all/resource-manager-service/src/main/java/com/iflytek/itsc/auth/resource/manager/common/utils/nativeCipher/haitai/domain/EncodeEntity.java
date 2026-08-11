package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.domain;

import lombok.Data;

/**
 * @Classname EncodeEntity
 * @Description 海泰机密性加密后返回的实体
 * @Date 2024/12/4 9:55
 * @Created by wxqiu
 */
@Data
public class EncodeEntity {
    /**
     * 编码类型:十六进制编码(HEX),BASE64 编
     * 码(BASE64
     */
    private String encode;
    /**
     * 填充方式 NoPadding、PKCS7Padding，默
     * 认使用 PKCS7Padding
     */
    private String padding;
    /**
     * 数据密钥唯一标识
     */
    private String keyId;
    /**
     * 密文数据
     */
    private String encData;
}
