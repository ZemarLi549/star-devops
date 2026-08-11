package com.iflytek.itsc.auth.resource.manager.common.annotation;

import com.iflytek.itsc.auth.resource.manager.domain.validator.NotEmptyThenNotBlankValidator;

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
@Constraint(validatedBy = {NotEmptyThenNotBlankValidator.class})
public @interface NotEmptyThenNotBlank {

    String message() default "集合元素为空";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
