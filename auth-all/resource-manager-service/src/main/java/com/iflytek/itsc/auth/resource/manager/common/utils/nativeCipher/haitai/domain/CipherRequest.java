package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.haitai.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname CipherForm
 * @Description 海泰机密性入参
 * @Date 2024/12/4 15:42
 * @Created by wxqiu
 */
@Data
@Builder
public class CipherRequest {

    /**
     * 据密钥唯一标识，参数为空时由平台指定，目前写在配置文件里，如果每次请求加密接口变动keyId，会导致加密后的字符串不一致。
     */
    private String keyId;

    /**
     *业务 ID，使用当前调用时间，格式为 1727334106（建议
     * 业务系统进行日志记录，方便根据 transId 排查问题）
     */
    private String transId;
    /**
     * 编码类型:十六进制编码(HEX),BASE64 编码(BASE64)
     */
    private String encode;
    /**
     * 对称加密算法:SM4
     */
    private String symEncAlg;
    /**
     * 加密模式 :ECB/CBC，默认使用CBC
     */
    private String mode;
    /**
     * 填 充 方 式 NoPadding 、 PKCS7Padding ， 默 认 使 用
     * PKCS7Padding
     */
    private String padding;
    /*
    需要加密的数据
     */
    private String data;
    
    
    
    
}
