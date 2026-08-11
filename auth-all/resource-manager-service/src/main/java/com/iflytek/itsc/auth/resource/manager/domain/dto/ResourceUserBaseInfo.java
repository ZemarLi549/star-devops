package com.iflytek.itsc.auth.resource.manager.domain.dto;

import lombok.Data;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 用户基础信息
 **/
@Data
public class ResourceUserBaseInfo {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户账号
     */
    private String account;
    /**
     * 密码
     */
    private String passwd;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 头像
     */
    private String headImg;
    /**
     * 手机号
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
     * 是否启用
     */
    private boolean isactive;
    /**
     * 是否登陆过
     */
    private boolean isloggedin;
    /**
     * 密码变更时间
     */
    private Long passwdModifyTime;
    /**
     * 新手指引状态
     */
    private String guideState;
}
