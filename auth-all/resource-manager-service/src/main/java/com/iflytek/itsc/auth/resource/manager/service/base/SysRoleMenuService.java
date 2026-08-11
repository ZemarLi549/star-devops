package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleMenuEntity;

import java.util.Collection;
import java.util.List;

public interface SysRoleMenuService extends MPJBaseService<SysRoleMenuEntity> {

    void deleteByMenuIds(Collection<Long> menuIds);

    List<Long> listMenuIdsBy(Collection<Long> roleIds);
    List<Long> listMenuIdsBy(Long roleId);

    void deleteByRoleId(Long roleId);

    List<SysRoleMenuEntity> listByRoleIds(Collection<Long> roleIds);
}