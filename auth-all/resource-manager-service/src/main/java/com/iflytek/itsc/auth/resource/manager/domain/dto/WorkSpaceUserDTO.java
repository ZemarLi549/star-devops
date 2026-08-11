package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc
 **/
@Data
public class WorkSpaceUserDTO implements Serializable {
    private static final long serialVersionUID = -649521829711840880L;

    private Long userId;
    /**
     * 账号
     */
    private String account;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 角色名称
     */
    private String roleName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 创建/更新时间
     */
    private String createTime;
}
