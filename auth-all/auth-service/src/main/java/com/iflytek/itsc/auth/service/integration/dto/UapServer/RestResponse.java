package com.iflytek.itsc.auth.service.integration.dto.UapServer;

import lombok.Data;

/**
 * @Classname RestResponse
 * @Description uap 公共响应
 * @Date 2024/1/23 9:51
 * @Created by wxqiu
 */
@Data
public class RestResponse<T> {

    private boolean flag;
    private String message;

    private T data;

    private String code;
}
