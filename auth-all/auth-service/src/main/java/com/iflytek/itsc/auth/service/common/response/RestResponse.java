package com.iflytek.itsc.auth.service.common.response;

import com.iflytek.itsc.auth.service.common.enums.ErrorCodeEnum;
import lombok.Data;

/**
 * 本地版本的RestResponse类，用于解决外部依赖缺失问题
 * 用于包装API响应数据
 */
@Data
public class RestResponse<T> {
    
    private Integer code;
    private String message;
    private T data;
    private boolean success;
    
    /**
     * 构建成功响应（带数据）
     */
    public static <T> RestResponse<T> buildSuccess(T data) {
        RestResponse<T> response = new RestResponse<>();
        response.setCode(0);
        response.setMessage("success");
        response.setData(data);
        response.setSuccess(true);
        return response;
    }
    
    /**
     * 构建成功响应（无数据）
     */
    public static RestResponse buildSuccess() {
        RestResponse response = new RestResponse<>();
        response.setCode(0);
        response.setMessage("success");
        response.setSuccess(true);
        return response;
    }
    
    /**
     * 构建错误响应
     */
    public static RestResponse buildError(ErrorCodeEnum errorCode) {
        RestResponse response = new RestResponse<>();
        response.setCode(Integer.valueOf(errorCode.getErrorCode()));
        response.setMessage(errorCode.getErrorMsg());
        response.setSuccess(false);
        return response;
    }
}