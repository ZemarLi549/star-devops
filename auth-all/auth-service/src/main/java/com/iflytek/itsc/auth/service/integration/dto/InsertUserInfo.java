package com.iflytek.itsc.auth.service.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import lombok.Data;

/**
 * @Classname InsertUserRequest
 * @Description 保存用户基础信息入参
 * @Date 2024/1/3 10:42
 * @Created by wxqiu
 */
@Data
public class InsertUserInfo {

    //userId 序列化的时候忽略
    @JsonIgnore
    private String userId;

    @JsonIgnore
    private String TenantId;

    private String account;

    private String nickName;

    private String email;

    private String phone;

    public SysUserForm toSysUserForm(InsertUserInfo insertUserInfo) {
        SysUserForm sysUserForm = new SysUserForm();
        sysUserForm.setAccount(insertUserInfo.getAccount());
        sysUserForm.setNickName(insertUserInfo.getNickName());
        sysUserForm.setEmail(insertUserInfo.getEmail());
        sysUserForm.setPhone(insertUserInfo.getPhone());
        return sysUserForm;

    }
}
