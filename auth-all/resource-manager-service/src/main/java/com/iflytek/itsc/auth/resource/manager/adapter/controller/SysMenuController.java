package com.iflytek.itsc.auth.resource.manager.adapter.controller;

import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysMenuService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysMenuForm;
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
 * @date 2023/12/8
 * @desc 菜单管理
 **/
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {
    @Autowired
    private InnerSysMenuService innerSysMenuService;

    /**
     * 新增菜单
     */
    @PostMapping("/insert")
    public RestResponse<Object> insert(@Validated(AddGroup.class) @RequestBody SysMenuForm form) {
        innerSysMenuService.insert(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 更新菜单
     */
    @PostMapping("/update")
    public RestResponse<Object> update(@Validated(UpdateGroup.class) @RequestBody SysMenuForm form) {
        innerSysMenuService.update(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 删除菜单
     */
    @PostMapping("/delete")
    public RestResponse<Object> delete(@Validated(DeleteGroup.class) @RequestBody SysMenuForm form) {
        innerSysMenuService.delete(form);
        return RestResponse.buildSuccess();
    }

    /**
     * 查询菜单详情
     */
    @GetMapping("/detail")
    public RestResponse<SysMenuForm> getDetail(Long menuId) {
        SysMenuForm detail = innerSysMenuService.getDetail(menuId);
        return RestResponse.buildSuccess(detail);
    }

    /**
     * 查询服务模块下菜单树
     */
    @GetMapping("/getModuleSubTree")
    public RestResponse<List<MenuTreeDTO>> getModuleSubTree(String module) {
        List<MenuTreeDTO> tree = innerSysMenuService.getModuleSubTree(module);
        return RestResponse.buildSuccess(tree);
    }
}
