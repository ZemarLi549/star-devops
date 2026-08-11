package com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher;

import com.iflytek.itsc.auth.resource.manager.common.annotation.IntegralityData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.IntegralityField;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * @Classname SM3HMACUtil
 * @Description 完整性工具类
 * @Date 2024/12/3 14:47
 * @Created by wxqiu
 */
@Slf4j
public abstract class SM3HMACUtil {

    /**
     * 生成完整性hash值
     * @param encryptContent
     * @return
     */
    public abstract String integralityEncode(String encryptContent);

    /**
     * 完整性验证
     * @param content 原文
     * @param sign 签名值
     * @return
     */
    public abstract boolean verifyIntegrality(String content, String sign);

    public String getOriginSign(Object finalObject) {
        StringBuilder originSign = new StringBuilder();
        try {
            boolean isContain = finalObject.getClass().isAnnotationPresent(IntegralityData.class);
            if(isContain){
                Field[] fields = finalObject.getClass().getDeclaredFields();
                for (Field field : fields) {
                    boolean isContainCiphertextField = field.isAnnotationPresent(IntegralityField.class);
                    if (isContainCiphertextField) {
                        field.setAccessible(true);
                        Object o = field.get(finalObject);
                        if (o != null) {
                            originSign.append(o);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("getOriginSign have exception:{}", e.getMessage(), e);
        }
        return originSign.toString();

    }

}
