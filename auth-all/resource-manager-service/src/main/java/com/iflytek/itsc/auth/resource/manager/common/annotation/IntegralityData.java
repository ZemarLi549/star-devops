package com.iflytek.itsc.auth.resource.manager.common.annotation;

import java.lang.annotation.*;

/**
 * @Classname CipherData
 * @Description 实体是否需要加解密注解 放到实体类上
 * @Date 2024/5/9 14:13
 * @Created by wxqiu
 */

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegralityData {
}
