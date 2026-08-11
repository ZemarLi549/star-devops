package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc 工作空间用户详情
 **/
@Data
public class WorkSpaceUserDetailDTO implements Serializable {
    private static final long serialVersionUID = -1192121748487967158L;

    private String workSpaceName;
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
    private Long roleId;
    private String roleName;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 邮箱
     */
    private String email;
}
