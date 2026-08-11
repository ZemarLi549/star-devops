package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class UserDTO {

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
     * 邮箱地址
     */
    private String email;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 飞书 user_id
     */
    private String feishuUserId;

    /**
     * 三方 IT 工作台用户 ID
     */
    private String itWorkbenchUserId;

    /**
     * 身份来源：LOCAL / LDAP / SYNC
     */
    private String identitySource;

    /**
     * LDAP 登录账号或映射账号
     */
    private String ldapAccount;

    /**
     * 头像
     */
    private String headImg;

    /**
     * 是否启用
     */
    private Boolean isactive;

    /**
     * 是否登录过
     */
    private Boolean isloggedin;

    /**
     * 是否超管
     */
    private Integer issuperadmin;

    /**
     * 最后登录时间
     */
    private Date loginTime;
    /**
     * 更新时间
     */
    private Date modifyTime;

    /**
     * 所属空间（角色）-权限管理用户池显示用
     */
    private List<String> spaceRole;
    /**
     * 所属空间（角色）-告警平台用户列表用
     */
    private Map<Long, String> spaceRoleMap;

    /**
     * 绑定的空间ids
     */
    private List<Long> workSpaceIdList;
}
