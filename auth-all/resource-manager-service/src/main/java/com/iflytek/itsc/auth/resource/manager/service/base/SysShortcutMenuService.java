package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ShortcutMenuDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysShortcutMenuEntity;

import java.util.Collection;
import java.util.List;

/**
 * @Classname SysShortcutMenuService
 * @Description 快捷菜单服务接口类
 * @Date 2024/8/22 14:05
 * @Created by wxqiu
 */
public interface SysShortcutMenuService extends MPJBaseService<SysShortcutMenuEntity> {

    List<ShortcutMenuDTO> findByAccountAndWorkspaceId(String account, Long workspaceId);
    
    void deleteByWorkspaceIdAndAccountAndMenuId(Long workspaceId, String account, Long menuId);


    void deleteByMenuIds(Collection<Long> menuIds);
    
}
