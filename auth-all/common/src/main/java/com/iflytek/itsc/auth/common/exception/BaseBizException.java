package com.iflytek.itsc.auth.common.exception;

import com.iflytek.itsc.auth.common.enums.BaseErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 本地版本的BaseBizException类，用于解决外部依赖缺失问题
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseBizException extends RuntimeException {
    
    private String errorCode;
    private String errorMsg;

    public BaseBizException(String errorMsg) {
        super(errorMsg);
        this.errorMsg = errorMsg;
    }

    public BaseBizException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public BaseBizException(String errorCode, String errorMsg, Object... arguments) {
        super(String.format(errorMsg, arguments));
        this.errorCode = errorCode;
        this.errorMsg = String.format(errorMsg, arguments);
    }
    
    public BaseBizException(BaseErrorCodeEnum baseErrorCodeEnum) {
        super(baseErrorCodeEnum.getErrorMsg());
        this.errorCode = baseErrorCodeEnum.getErrorCode();
        this.errorMsg = baseErrorCodeEnum.getErrorMsg();
    }
    
    public BaseBizException(BaseErrorCodeEnum baseErrorCodeEnum, Object... arguments) {
        super(String.format(baseErrorCodeEnum.getErrorMsg(), arguments));
        this.errorCode = baseErrorCodeEnum.getErrorCode();
        this.errorMsg = String.format(baseErrorCodeEnum.getErrorMsg(), arguments);
    }
}