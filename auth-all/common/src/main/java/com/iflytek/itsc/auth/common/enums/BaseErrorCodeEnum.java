package com.iflytek.itsc.auth.common.enums;

/**
 * 错误码枚举基类接口
 */
public interface BaseErrorCodeEnum {
    
    /**
     * 获取错误码
     */
    String getErrorCode();
    
    /**
     * 获取错误信息
     */
    String getErrorMsg();
}