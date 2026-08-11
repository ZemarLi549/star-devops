package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysUserService;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.UserDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserDeleteForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author xdkong2
 * @date 2023/11/30
 * @desc
 **/
@RestController
@RequestMapping("/sys/user")
public class SysUserController {
    @Autowired
    private InnerSysUserService innerSysUserService;

    /**
     * 新增用户，用户池新增不用判断空间权限
     */
    @PostMapping("/insert")
    public RestResponse<Object> insertUser(@Validated(AddGroup.class) @RequestBody SysUserForm form) {
        innerSysUserService.insertUser(form, false);
        return RestResponse.buildSuccess();
    }

    /**
     * 更新用户
     */
    @PostMapping("/update")
    public RestResponse<Object> updateUser(@Validated(UpdateGroup.class) @RequestBody SysUserForm form) {
        innerSysUserService.updateUser(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete")
    public RestResponse<Object> deleteUser(@RequestBody SysUserDeleteForm form) {
        innerSysUserService.deleteUser(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 变更启用状态
     */
    @PostMapping("/changeActive")
    public RestResponse<Object> changeActive(@RequestBody SysUserForm form) {
        innerSysUserService.changeActive(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 重置密码
     */
    @PostMapping("/resetPassword")
    public RestResponse<Object> resetPassword(@RequestBody SysUserPageParam form) {
        innerSysUserService.resetPassword(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    public RestResponse<Object> updatePassword(@RequestBody SysUserPageParam form) {
        innerSysUserService.updatePassword(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 分页查询
     */
    @PostMapping("/page")
    public RestResponse<Page<UserDTO>> page(@RequestBody SysUserPageParam form) {

        Page<UserDTO> list = innerSysUserService.page(form);
        return RestResponse.buildSuccess(list);
    }

    /**
     * 查询初始密码
     */
    @GetMapping("/getDefaultPwd")
    public RestResponse<String> getDefaultPwd() {
        String passwd = innerSysUserService.getDefaultPwd();
        return RestResponse.buildSuccess(passwd);
    }
}
