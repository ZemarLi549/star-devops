package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserRoleService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserRoleEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends MPJBaseServiceImpl<SysUserRoleMapper, SysUserRoleEntity> implements SysUserRoleService {

    @Override
    public void deleteByUserIds(Collection<Long> userIds) {
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUserRoleEntity::getUserId, userIds);
        this.remove(wrapper);
    }

    @Override
    public void insert(Long userId, Long roleId, Long workSpaceId) {
        SysUserRoleEntity entity = new SysUserRoleEntity();
        entity.setUserId(userId);
        entity.setRoleId(roleId);
        entity.setWorkSpaceId(workSpaceId);
        this.save(entity);
    }

    @Override
    public List<Long> listRoleIdsBy(Long userId, Long workSpaceId) {
        List<SysUserRoleEntity> list = this.lambdaQuery()
                .eq(SysUserRoleEntity::getUserId, userId)
                .eq(SysUserRoleEntity::getWorkSpaceId, workSpaceId)
                .select(SysUserRoleEntity::getRoleId)
                .list();
        return list.stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toList());
    }

    @Override
    public List<SysUserRoleEntity> listByRoleId(Long roleId) {
        return this.lambdaQuery()
                .eq(SysUserRoleEntity::getRoleId, roleId)
                .list();
    }

    @Override
    public void deleteByWorkSpaceId(Long workSpaceId) {
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getWorkSpaceId, workSpaceId);
        this.remove(wrapper);
    }

    @Override
    public void deleteBy(Long userId, Long workSpaceId) {
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getWorkSpaceId, workSpaceId);
        wrapper.eq(SysUserRoleEntity::getUserId, userId);
        this.remove(wrapper);
    }

    @Override
    public void deleteByUserIdSpaceIds(Long userId, Collection<Long> spaceIds) {
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getUserId, userId);
        wrapper.in(SysUserRoleEntity::getWorkSpaceId, spaceIds);
        this.remove(wrapper);
    }

    @Override
    public void deleteBySpaceIdUserIds(Long workSpaceId, Collection<Long> userIds) {
        LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRoleEntity::getWorkSpaceId, workSpaceId);
        wrapper.in(SysUserRoleEntity::getUserId, userIds);
        this.remove(wrapper);
    }

    @Override
    public List<SysUserRoleEntity> listBy(Long workSpaceId, List<Long> roleIds) {
        return this.lambdaQuery()
                .eq(SysUserRoleEntity::getWorkSpaceId, workSpaceId)
                .in(SysUserRoleEntity::getRoleId, roleIds)
                .list();
    }

    @Override
    public Map<Long, String> listSpaceUserRoleName(Long userId) {
        MPJLambdaWrapper<SysUserRoleEntity> wrapper = new MPJLambdaWrapper<SysUserRoleEntity>()
                .select(SysUserRoleEntity::getWorkSpaceId)
                .select(SysRoleEntity::getRoleName)
                .leftJoin(SysRoleEntity.class, SysRoleEntity::getRoleId, SysUserRoleEntity::getRoleId)
                .eq(SysUserRoleEntity::getUserId, userId)
                .isNotNull(SysUserRoleEntity::getWorkSpaceId);
        List<SysUserRoleEntity> list = this.baseMapper.selectList(wrapper);

        return list.stream()
                .collect(Collectors.groupingBy(SysUserRoleEntity::getWorkSpaceId,
                        Collectors.mapping(SysUserRoleEntity::getRoleName, Collectors.joining(","))));
    }

    @Override
    public List<SysUserRoleEntity> listByUserIds(Collection<Long> userIds) {
        return this.lambdaQuery()
                .in(SysUserRoleEntity::getUserId, userIds)
                .list();
    }

    @Override
    public List<SysUserRoleEntity> listByUserId(Long userId) {
        return this.lambdaQuery()
                .eq(SysUserRoleEntity::getUserId, userId)
                .list();
    }

    @Override
    public Long getRoleIdByUserIdAndWorkspaceId(Long userId, Long workspaceId) {
        SysUserRoleEntity sysUserRoleEntity = this.lambdaQuery().
                select(SysUserRoleEntity::getRoleId)
                .eq(SysUserRoleEntity::getUserId, userId)
                .eq(SysUserRoleEntity::getWorkSpaceId, workspaceId).one();
        if (null != sysUserRoleEntity) {
           return sysUserRoleEntity.getRoleId();
        }
        return null;
    }
}