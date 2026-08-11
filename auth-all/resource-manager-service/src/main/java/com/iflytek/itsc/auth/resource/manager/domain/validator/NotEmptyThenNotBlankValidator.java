package com.iflytek.itsc.auth.resource.manager.domain.validator;

import com.iflytek.itsc.auth.resource.manager.common.annotation.NotEmptyThenNotBlank;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 集合无blank元素校验
 **/
public class NotEmptyThenNotBlankValidator implements ConstraintValidator<NotEmptyThenNotBlank, Iterable<String>> {

    @Override
    public boolean isValid(Iterable<String> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (String next : value) {
            if (StringUtils.isBlank(next)) {
                return false;
            }
        }
        return true;
    }
}
