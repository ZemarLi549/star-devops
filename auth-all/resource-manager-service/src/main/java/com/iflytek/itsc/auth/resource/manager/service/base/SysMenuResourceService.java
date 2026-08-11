package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuResourceEntity;

import java.util.Collection;
import java.util.List;

public interface SysMenuResourceService extends MPJBaseService<SysMenuResourceEntity> {

    void deleteByMenuId(Long menuId);

    List<SysMenuResourceEntity> listByMenuId(Long menuId);

    void deleteByMenuIds(Collection<Long> menuIds);

    List<SysMenuResourceEntity> listByRoleId(Long roleId);
}