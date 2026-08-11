package com.iflytek.itsc.auth.resource.manager.domain.form;

import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/5
 * @desc
 **/
@Data
@Valid
public class SysUserForm implements Serializable {
    private static final long serialVersionUID = -7351972304819861223L;

    /**
     * 用户id
     */
    @NotNull(message = "userId不能为空", groups = {UpdateGroup.class})
    private Long userId;

    /**
     * 账号
     */
    @NotBlank(message = "登录账号不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @Length(max = 100, message = "登录账号长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String account;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 昵称
     */
    @Length(max = 100, message = "昵称长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String nickName;

    /**
     * 邮箱地址
     */
    @Length(max = 100, message = "邮箱长度不能超过100", groups = {AddGroup.class, UpdateGroup.class})
    private String email;

    /**
     * 联系方式
     */
    @Length(max = 20, message = "电话长度不能超过20", groups = {AddGroup.class, UpdateGroup.class})
    private String phone;

    /**
     * 飞书 user_id
     */
    @Length(max = 128, message = "飞书 user_id 长度不能超过128", groups = {AddGroup.class, UpdateGroup.class})
    private String feishuUserId;

    /**
     * 三方 IT 工作台用户 ID
     */
    @Length(max = 128, message = "三方 IT 工作台 ID 长度不能超过128", groups = {AddGroup.class, UpdateGroup.class})
    private String itWorkbenchUserId;

    /**
     * 身份来源：LOCAL / LDAP / SYNC
     */
    @Length(max = 20, message = "身份来源长度不能超过20", groups = {AddGroup.class, UpdateGroup.class})
    private String identitySource;

    /**
     * LDAP 登录账号或映射账号
     */
    @Length(max = 128, message = "LDAP 账号长度不能超过128", groups = {AddGroup.class, UpdateGroup.class})
    private String ldapAccount;

    /**
     * 是否启用 变更启用状态用
     */
    private Boolean isactive;

    /**
     * 工作空间
     */
    private List<Long> workSpaceIdList;
}
