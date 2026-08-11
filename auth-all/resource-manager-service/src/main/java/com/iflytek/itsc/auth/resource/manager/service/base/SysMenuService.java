package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SysMenuService extends MPJBaseService<SysMenuEntity> {
    /**
     * 查询所有菜单
     */
    List<SysMenuEntity> listValid();

    void deleteByMenuIds(Collection<Long> menuIds);

    List<SysMenuEntity> listByModule(String module);

    /**
     * 查询登录用户权限内的菜单
     */
    List<MenuTreeDTO> getAuthTree(Long userId, Long workSpaceId);

    /**
     * 查询默认菜单
     */
    Set<String> listDefaultIds();

    List<SysMenuEntity> listValidByRoleId(Long roleId);

    List<SysMenuEntity> findByMenuNo(List<String> menuNos);

    /**
     * 查询当前最大的菜单id
     */
    Long getMaxMenuId();
}