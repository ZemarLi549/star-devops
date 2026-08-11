package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysWorkSpaceMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysRoleService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysWorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysWorkSpaceServiceImpl extends MPJBaseServiceImpl<SysWorkSpaceMapper, SysWorkSpaceEntity> implements SysWorkSpaceService {
    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询所有有效工作组
     */
    @Override
    public List<SysWorkSpaceEntity> listValid() {
        return this.lambdaQuery()
                .eq(SysWorkSpaceEntity::getIsvalid, true)
                .list();
    }

    @Override
    public List<SysWorkSpaceEntity> listValidByUserId(Long userId) {
        MPJLambdaWrapper<SysWorkSpaceEntity> wrapper = new MPJLambdaWrapper<SysWorkSpaceEntity>()
                .distinct()
                .selectAll(SysWorkSpaceEntity.class)
                .innerJoin(SysUserRoleEntity.class, SysUserRoleEntity::getWorkSpaceId, SysWorkSpaceEntity::getWorkSpaceId)
                .eq(SysUserRoleEntity::getUserId,userId);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Map<Long, Long> listSpaceUserCount() {
        MPJLambdaWrapper<SysWorkSpaceEntity> wrapper = new MPJLambdaWrapper<SysWorkSpaceEntity>()
                .select(SysWorkSpaceEntity::getWorkSpaceId)
                .selectCount(SysUserWorkSpaceEntity::getUserId, SysWorkSpaceEntity::getNum)
                .leftJoin(SysUserWorkSpaceEntity.class, SysUserWorkSpaceEntity::getWorkSpaceId, SysWorkSpaceEntity::getWorkSpaceId)
                .eq(SysWorkSpaceEntity::getIsvalid, true)
                .groupBy(SysWorkSpaceEntity::getWorkSpaceId);
        List<SysWorkSpaceEntity> list = this.baseMapper.selectList(wrapper);

        return list.stream().collect(Collectors.toMap(SysWorkSpaceEntity::getWorkSpaceId, SysWorkSpaceEntity::getNum));
    }

    @Override
    public Map<Long, Long> listSpaceRoleCount() {
        // 查询各空间关联角色数量，默认0
        MPJLambdaWrapper<SysWorkSpaceEntity> wrapper = new MPJLambdaWrapper<SysWorkSpaceEntity>()
                .select(SysWorkSpaceEntity::getWorkSpaceId)
                .selectCount(SysRoleEntity::getRoleId, SysWorkSpaceEntity::getNum)
                .leftJoin(SysRoleEntity.class, on -> on
                        .eq(SysWorkSpaceEntity::getWorkSpaceId, SysRoleEntity::getWorkSpaceId)
                        .eq(SysRoleEntity::getIsvalid, true)
                )
                .eq(SysWorkSpaceEntity::getIsvalid, true)
                .groupBy(SysWorkSpaceEntity::getWorkSpaceId);
        List<SysWorkSpaceEntity> spaceRoleCount = this.baseMapper.selectList(wrapper);

        // 查询默认角色数量
        Long count = sysRoleService.lambdaQuery()
                .eq(SysRoleEntity::getIsvalid, true)
                .eq(SysRoleEntity::getIsdefault, true)
                .count();
        Map<Long, Long> result = new HashMap<>(spaceRoleCount.size());
        for (SysWorkSpaceEntity dto : spaceRoleCount) {
            result.put(dto.getWorkSpaceId(), dto.getNum() + count);
        }
        return result;
    }
}