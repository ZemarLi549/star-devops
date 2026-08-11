package com.iflytek.itsc.auth.service.domain.dto;

import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname UserUpdateRequest
 * @Description 用户信息修改入参
 * @Date 2023/12/23 16:44
 * @Created by wxqiu
 */

@Data
public class UserUpdateRequest {

    @NotNull(message = "userId 不能为空")
    private String userId;

    @NotBlank(message = "account 不能为空")
    private String account;

    @NotBlank(message = "nickName不能为空")
    private String nickName;
    @NotBlank(message = "email不能为空")
    private String email;
    @NotBlank(message = "phone 不能为空")
    private String phone;

    public SysUserForm toSysUserForm(UserUpdateRequest userUpdateRequest){
        SysUserForm sysUserForm = new SysUserForm();
        sysUserForm.setUserId(Long.valueOf(userUpdateRequest.getUserId()));
        sysUserForm.setAccount(userUpdateRequest.getAccount());
        sysUserForm.setNickName(userUpdateRequest.getNickName());
        sysUserForm.setEmail(userUpdateRequest.getEmail());
        sysUserForm.setPhone(userUpdateRequest.getPhone());
        return sysUserForm;

    }

}
