package com.iflytek.itsc.auth.service.domain.dto;

import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Classname PasswordUpdateRequest
 * @Description 用户密码修改入参
 * @Date 2023/12/23 16:44
 * @Created by wxqiu
 */

@Data
public class PasswordUpdateRequest {


    @NotNull(message = "userId 不能为空")
    private String userId;

    @NotBlank(message = "passwd 不能为空")
    private String passwd;

    @NotBlank(message = "oldPwd 不能为空")
    private String oldPwd;

    public SysUserPageParam toSysUserPageParam(PasswordUpdateRequest passwordUpdateRequest){
        SysUserPageParam sysUserPageParam = new SysUserPageParam();
        sysUserPageParam.setUserId(passwordUpdateRequest.getUserId());
        sysUserPageParam.setPasswd(passwordUpdateRequest.getPasswd());
        sysUserPageParam.setOldPwd(passwordUpdateRequest.getOldPwd());
        return sysUserPageParam;
    }

}
