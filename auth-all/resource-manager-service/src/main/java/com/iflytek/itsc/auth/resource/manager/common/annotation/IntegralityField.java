package com.iflytek.itsc.auth.resource.manager.common.annotation;

import java.lang.annotation.*;

/**
 * @Classname IntegralityField
 * @Description 标识哪些字段作为生成sign值的原始字段
 * @Date 2024/5/9 14:26
 * @Created by wxqiu
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IntegralityField {
}
