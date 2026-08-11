package com.iflytek.itsc.auth.resource.manager.service.inner;

import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysMenuForm;

import java.util.List;

public interface InnerSysMenuService {

    /**
     * 新增菜单
     */
    void insert(SysMenuForm form);

    /**
     * 更新菜单
     */
    void update(SysMenuForm form);

    /**
     * 删除菜单
     */
    void delete(SysMenuForm form);

    /**
     * 查询菜单详情
     */
    SysMenuForm getDetail(Long menuId);

    /**
     * 查询全部菜单树
     */
    List<MenuTreeDTO> getModuleSubTree(String module);
    
    void saveBatch(List<SysMenuForm> sysMenuForms);

    void deleteBatch(String moduleName);
}
