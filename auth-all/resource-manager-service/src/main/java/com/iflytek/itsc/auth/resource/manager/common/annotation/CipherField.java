package com.iflytek.itsc.auth.resource.manager.common.annotation;

import java.lang.annotation.*;

/**
 * @Classname CipherFiled
 * @Description 字段加解密注解 放到需要加解密的字段上
 * @Date 2024/5/9 14:26
 * @Created by wxqiu
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CipherField {
}
