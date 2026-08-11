package com.iflytek.itsc.auth.resource.manager.common.exception;

import com.iflytek.itsc.auth.common.enums.BaseErrorCodeEnum;
import com.iflytek.itsc.auth.common.exception.BaseBizException;

/**
 * @Classname AuthBizException
 * @Description TODO
 * @Date 2024/10/29 10:27
 * @Created by wxqiu
 */
public class AuthBizException extends BaseBizException {

    public AuthBizException(String errorMsg) {
        super(errorMsg);
    }

    public AuthBizException(String errorCode, String errorMsg) {
        super(errorCode, errorMsg);
    }

    public AuthBizException(BaseErrorCodeEnum baseErrorCodeEnum) {
        super(baseErrorCodeEnum);
    }

    public AuthBizException(String errorCode, String errorMsg, Object... arguments) {
        super(errorCode, errorMsg, arguments);
    }

    public AuthBizException(BaseErrorCodeEnum baseErrorCodeEnum, Object... arguments) {
        super(baseErrorCodeEnum, arguments);
    }
}
