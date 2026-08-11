package com.iflytek.itsc.auth.resource.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.CipherField;
import com.iflytek.itsc.auth.resource.manager.common.annotation.IntegralityData;
import com.iflytek.itsc.auth.resource.manager.common.annotation.IntegralityField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 */
@Data
@TableName("sys_user")
@CipherData
@IntegralityData
public class SysUserEntity implements Serializable {
    private static final long serialVersionUID = -8412604347341385793L;
    /**
     * 用户id
     */
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long userId;

    @IntegralityField
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
     * 邮箱地址
     */
    @CipherField
    @IntegralityField
    private String email;

    /**
     * 联系方式
     */
    @CipherField
    @IntegralityField
    private String phone;

    /**
     * 飞书 user_id
     */
    @TableField("feishu_user_id")
    private String feishuUserId;

    /**
     * 三方 IT 工作台用户 ID
     */
    @TableField("it_workbench_user_id")
    private String itWorkbenchUserId;

    /**
     * 身份来源：LOCAL / LDAP / SYNC
     */
    @TableField("identity_source")
    private String identitySource;

    /**
     * LDAP 登录账号或映射账号
     */
    @TableField("ldap_account")
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
     * 用户最后一次登录时间
     */
    private Date loginTime;

    /**
     * 密码变更时间
     */
    private Date passwdModifyTime;

    /**
     * 是否有效
     */
    private Boolean isvalid;

    /**
     * 创建者
     */
    private Long createUser;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long modifyUser;

    /**
     * 更新时间
     */
    private Date modifyTime;

    /**
     * 新手指引状态
     */
    private String guideState;

    /*
    利川数据完整性校验字段
     */
    private String sign;
}
