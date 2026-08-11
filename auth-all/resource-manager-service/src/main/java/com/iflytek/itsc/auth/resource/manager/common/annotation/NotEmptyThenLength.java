package com.iflytek.itsc.auth.resource.manager.common.annotation;

import com.iflytek.itsc.auth.resource.manager.domain.validator.NotEmptyThenLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author xdkong2
 * @date 2023/12/4
 * @desc 如果String类型集合非空(不是非null)，则其内元素不能为空
 **/
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {NotEmptyThenLengthValidator.class})
public @interface NotEmptyThenLength {
    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message() default "集合元素长度";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
