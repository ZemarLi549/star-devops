package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.iflytek.itsc.auth.resource.manager.service.base.SysRoleMenuService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleMenuEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysRoleMenuMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleMenuServiceImpl extends MPJBaseServiceImpl<SysRoleMenuMapper, SysRoleMenuEntity> implements SysRoleMenuService {

    @Override
    public void deleteByMenuIds(Collection<Long> menuIds) {
        LambdaQueryWrapper<SysRoleMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysRoleMenuEntity::getMenuId, menuIds);
        this.remove(wrapper);
    }

    @Override
    public List<Long> listMenuIdsBy(Collection<Long> roleIds) {
        List<SysRoleMenuEntity> list = this.lambdaQuery()
                .in(SysRoleMenuEntity::getRoleId, roleIds)
                .select(SysRoleMenuEntity::getMenuId)
                .list();
        return list.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toList());
    }

    @Override
    public List<Long> listMenuIdsBy(Long roleId) {
        List<SysRoleMenuEntity> list = this.lambdaQuery()
                .eq(SysRoleMenuEntity::getRoleId, roleId)
                .select(SysRoleMenuEntity::getMenuId)
                .list();
        return list.stream().map(SysRoleMenuEntity::getMenuId).collect(Collectors.toList());
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        LambdaQueryWrapper<SysRoleMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuEntity::getRoleId, roleId);
        this.remove(wrapper);
    }

    @Override
    public List<SysRoleMenuEntity> listByRoleIds(Collection<Long> roleIds) {
        return this.lambdaQuery()
                .in(SysRoleMenuEntity::getRoleId, roleIds)
                .list();
    }
}