package com.iflytek.itsc.auth.resource.manager.domain.validator;

import com.iflytek.itsc.auth.resource.manager.common.annotation.NotEmptyThenLength;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 如果List<String>不为空，则每个元素长度需在范围值
 **/
public class NotEmptyThenLengthValidator implements ConstraintValidator<NotEmptyThenLength, Iterable<String>> {
    private int min;
    private int max;

    @Override
    public void initialize(NotEmptyThenLength constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Iterable<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String next : value) {
            if (StringUtils.isBlank(next)) {
                continue;
            }
            if (next.length() > max || next.length() < min) {
                return false;
            }
        }
        return true;
    }
}
