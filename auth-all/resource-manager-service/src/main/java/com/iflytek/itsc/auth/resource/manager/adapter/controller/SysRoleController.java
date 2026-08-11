package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysRoleService;
import com.iflytek.itsc.auth.resource.manager.domain.vo.RoleQueryParam;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.RoleDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysRoleForm;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.AddGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.DeleteGroup;
import com.iflytek.itsc.auth.resource.manager.domain.validator.group.UpdateGroup;
import com.iflytek.itsc.web.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 角色管理
 **/
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
    @Autowired
    private InnerSysRoleService innerSysRoleService;

    /**
     * 新增角色
     */
    @PostMapping("/insert")
    public RestResponse<Object> insert(@Validated(AddGroup.class) @RequestBody SysRoleForm form) {
        innerSysRoleService.insert(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 更新角色
     */
    @PostMapping("/update")
    public RestResponse<Object> update(@Validated(UpdateGroup.class) @RequestBody SysRoleForm form) {
        innerSysRoleService.update(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 删除角色
     */
    @PostMapping("/delete")
    public RestResponse<Object> delete(@Validated(DeleteGroup.class) @RequestBody SysRoleForm form) {
        innerSysRoleService.delete(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 分页查询角色列表
     */
    @PostMapping("/page")
    public RestResponse<Page<RoleDTO>> page(@RequestBody RoleQueryParam param) {
        Page<RoleDTO> page = innerSysRoleService.page(param);
        return RestResponse.buildSuccess(page);
    }

    /**
     * 查询参考角色列表
     */
    @GetMapping("/listTemplate")
    public RestResponse<List<KeyValueDTO>> listTemplate() {
        List<KeyValueDTO> list = innerSysRoleService.listTemplate();
        return RestResponse.buildSuccess(list);
    }

    /**
     * 查询所有角色列表
     */
    @GetMapping("/listSpaceTotal")
    public RestResponse<List<KeyValueDTO>> listSpaceTotal(Long workSpaceId) {
        List<KeyValueDTO> list = innerSysRoleService.listSpaceTotal(workSpaceId);
        return RestResponse.buildSuccess(list);
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/detail")
    public RestResponse<RoleDTO> getDetail(Long roleId) {
        RoleDTO dto = innerSysRoleService.getDetail(roleId);
        return RestResponse.buildSuccess(dto);
    }

    /**
     * 查询授权菜单树
     */
    @GetMapping("/getAuthMenuTree")
    public RestResponse<List<MenuTreeDTO>> getMenuTree(Long workSpaceId, Long roleId) {
        List<MenuTreeDTO> list = innerSysRoleService.getAuthMenuTree(workSpaceId, roleId);
        return RestResponse.buildSuccess(list);
    }
}
