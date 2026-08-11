package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher;

/**
 * @Classname SM4CBCUtil
 * @Description 机密性工具类
 * @Date 2024/12/3 14:52
 * @Created by wxqiu
 */
public abstract class SM4CBCUtil {
    /**
     * 数据机密性加密
     * @param cleartext
     * @return
     */
    public abstract String encode(String cleartext);

    /**
     * 数据机密性解密
     * @param encryptStr
     * @return
     */
    public abstract String decode(String encryptStr);


}
