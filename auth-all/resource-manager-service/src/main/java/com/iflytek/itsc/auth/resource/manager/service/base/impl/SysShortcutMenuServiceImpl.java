package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ShortcutMenuDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysShortcutMenuEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysShortcutMenuMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysShortcutMenuService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @Classname SysShortcutMenuServiceImpl
 * @Description 快捷菜单服务实现类
 * @Date 2024/8/22 14:07
 * @Created by wxqiu
 */
@Service
public class SysShortcutMenuServiceImpl extends MPJBaseServiceImpl<SysShortcutMenuMapper, SysShortcutMenuEntity> implements SysShortcutMenuService {
    @Override
    public List<ShortcutMenuDTO> findByAccountAndWorkspaceId(String account, Long workspaceId) {
        MPJLambdaWrapper<SysShortcutMenuEntity> wrapper = new MPJLambdaWrapper<SysShortcutMenuEntity>()
                .selectAll(SysShortcutMenuEntity.class)
                .select(SysMenuEntity::getMenuName)
                .leftJoin(SysMenuEntity.class, SysMenuEntity::getMenuId, SysShortcutMenuEntity::getMenuId)
                .eq(SysShortcutMenuEntity::getAccount, account)
                .eq(SysShortcutMenuEntity::getWorkspaceId, workspaceId)
                .orderByDesc(SysShortcutMenuEntity::getCreateTime);
         List<ShortcutMenuDTO> shortcutMenuDTOS = this.baseMapper.selectJoinList(ShortcutMenuDTO.class, wrapper);

        return shortcutMenuDTOS;

        
    }

    @Override
    public void deleteByWorkspaceIdAndAccountAndMenuId(Long workspaceId, String account, Long menuId) {
        this.lambdaUpdate()
                .eq(SysShortcutMenuEntity::getWorkspaceId,workspaceId)
                .eq(SysShortcutMenuEntity::getAccount,account)
                .eq(SysShortcutMenuEntity::getMenuId,menuId).remove();

    }

    @Override
    public void deleteByMenuIds(Collection<Long> menuIds) {
        LambdaQueryWrapper<SysShortcutMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysShortcutMenuEntity::getMenuId, menuIds);
        this.remove(wrapper);
    }
}
