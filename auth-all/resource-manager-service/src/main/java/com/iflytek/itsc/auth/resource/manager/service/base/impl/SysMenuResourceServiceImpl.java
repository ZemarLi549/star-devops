package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuResourceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserRoleEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysMenuResourceMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysMenuResourceService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuResourceServiceImpl extends MPJBaseServiceImpl<SysMenuResourceMapper, SysMenuResourceEntity> implements SysMenuResourceService {

    @Override
    public void deleteByMenuId(Long menuId) {
        LambdaQueryWrapper<SysMenuResourceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenuResourceEntity::getMenuId, menuId);
        this.remove(wrapper);
    }

    @Override
    public List<SysMenuResourceEntity> listByMenuId(Long menuId) {
        return this.lambdaQuery()
                .eq(SysMenuResourceEntity::getMenuId, menuId)
                .list();
    }

    @Override
    public void deleteByMenuIds(Collection<Long> menuIds) {
        LambdaQueryWrapper<SysMenuResourceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenuResourceEntity::getMenuId, menuIds);
        this.remove(wrapper);
    }

    @Override
    public List<SysMenuResourceEntity> listByRoleId(Long roleId) {
        MPJLambdaWrapper<SysMenuResourceEntity> wrapper = new MPJLambdaWrapper<SysMenuResourceEntity>()
                .select(SysMenuResourceEntity::getPath)
                .select(SysMenuResourceEntity::getMethod)
                .leftJoin(SysRoleMenuEntity.class, SysRoleMenuEntity::getMenuId, SysMenuResourceEntity::getMenuId)
                .eq(SysRoleMenuEntity::getRoleId, roleId);
        List<SysMenuResourceEntity> list = this.baseMapper.selectList(wrapper);

        return list;
    }
}