package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysUserWorkSpaceMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserWorkSpaceService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class SysUserWorkSpaceServiceImpl extends MPJBaseServiceImpl<SysUserWorkSpaceMapper, SysUserWorkSpaceEntity> implements SysUserWorkSpaceService {

    @Override
    public void insert(Long userId, Long workSpaceId) {
        SysUserWorkSpaceEntity entity = new SysUserWorkSpaceEntity();
        entity.setUserId(userId);
        entity.setWorkSpaceId(workSpaceId);
        entity.setCreateTime(new Date());
        this.save(entity);
    }

    @Override
    public void deleteByWorkSpaceId(Long workGroupId) {
        LambdaQueryWrapper<SysUserWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserWorkSpaceEntity::getWorkSpaceId, workGroupId);
        this.remove(wrapper);
    }

    @Override
    public void deleteByUserIds(Collection<Long> userIds) {
        LambdaQueryWrapper<SysUserWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysUserWorkSpaceEntity::getUserId, userIds);
        this.remove(wrapper);
    }

    @Override
    public List<SysUserWorkSpaceEntity> listByWorkSpaceIds(Collection<Long> ids) {
        return this.lambdaQuery()
                .in(SysUserWorkSpaceEntity::getWorkSpaceId, ids)
                .list();
    }

    @Override
    public void deleteBy(Long userId, Long workSpaceId) {
        LambdaQueryWrapper<SysUserWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserWorkSpaceEntity::getUserId, userId);
        wrapper.eq(SysUserWorkSpaceEntity::getWorkSpaceId, workSpaceId);
        this.remove(wrapper);
    }

    @Override
    public void deleteBySpaceIdUserIds(Long workSpaceId, Collection<Long> userIds) {
        LambdaQueryWrapper<SysUserWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserWorkSpaceEntity::getWorkSpaceId, workSpaceId);
        wrapper.in(SysUserWorkSpaceEntity::getUserId, userIds);
        this.remove(wrapper);
    }

    @Override
    public void deleteByUserIdSpaceIds(Long userId, Collection<Long> spaceIds) {
        LambdaQueryWrapper<SysUserWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserWorkSpaceEntity::getUserId, userId);
        wrapper.in(SysUserWorkSpaceEntity::getWorkSpaceId, spaceIds);
        this.remove(wrapper);
    }

    @Override
    public Long countByUser(Long userId) {
        return this.lambdaQuery()
                .eq(SysUserWorkSpaceEntity::getUserId, userId)
                .count();
    }

    @Override
    public Set<Long> listSpaceIdByUserId(Long userId) {
        List<SysUserWorkSpaceEntity> list = this.lambdaQuery()
                .eq(SysUserWorkSpaceEntity::getUserId, userId)
                .select(SysUserWorkSpaceEntity::getWorkSpaceId)
                .list();
        return list.stream().map(SysUserWorkSpaceEntity::getWorkSpaceId).collect(Collectors.toSet());
    }

    @Override
    public List<SysUserWorkSpaceEntity> listByUserIds(Collection<Long> userIds) {
        return this.lambdaQuery()
                .in(SysUserWorkSpaceEntity::getUserId, userIds)
                .list();
    }
}