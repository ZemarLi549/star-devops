package com.iflytek.itsc.auth.service.integration.dto.UapServer;

import lombok.Data;

/**
 * @Classname UapUserInfo
 * @Description uap 用户基本信息
 * @Date 2024/1/23 11:31
 * @Created by wxqiu
 */
@Data
public class UapUserInfo {

    private String id;

    private String name;

    private String loginName;

    private String email;

    private String phone;
}
