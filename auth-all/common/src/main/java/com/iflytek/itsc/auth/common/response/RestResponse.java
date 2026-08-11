package com.iflytek.itsc.auth.common.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应格式类
 */
@Data
public class RestResponse<T> {
    
    private Map<String, Object> config = new HashMap<>();
    private ResponseData<T> data;
    private int status = 200; // 默认返回200，错误时返回非200

    private RestResponse(ResponseData<T> data) {
        this.data = data;
    }

    /**
     * 构建成功响应
     */
    public static <T> RestResponse<T> success(T result) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setSuccess(true);
        responseData.setResult(result);
        return new RestResponse<>(responseData);
    }

    /**
     * 构建错误响应
     */
    public static <T> RestResponse<T> buildError(String errorCode, String errorMsg) {
        ResponseData<T> responseData = new ResponseData<>();
        responseData.setSuccess(false);
        responseData.setErrorCode(errorCode);
        responseData.setErrorMessage(errorMsg);
        RestResponse<T> response = new RestResponse<>(responseData);
        // 验证失败时设置非200状态码
        try {
            int code = Integer.parseInt(errorCode);
            if (code >= 400) {
                response.setStatus(code);
            } else {
                response.setStatus(400); // 默认使用400表示客户端错误
            }
        } catch (NumberFormatException e) {
            response.setStatus(400); // 默认使用400表示客户端错误
        }
        return response;
    }

    /**
     * 构建错误响应（默认错误码500）
     */
    public static <T> RestResponse<T> buildError(String errorMsg) {
        return buildError("500", errorMsg);
    }

    /**
     * 内部数据响应类
     */
    @Data
    public static class ResponseData<T> {
        private boolean success;
        private String errorCode;
        private String errorMessage;
        private T result;
    }
}