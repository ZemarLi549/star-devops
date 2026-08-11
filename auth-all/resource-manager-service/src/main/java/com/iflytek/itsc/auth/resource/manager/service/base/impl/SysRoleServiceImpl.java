package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.iflytek.itsc.auth.resource.manager.mapper.SysRoleMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysRoleService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRoleServiceImpl extends MPJBaseServiceImpl<SysRoleMapper, SysRoleEntity> implements SysRoleService {

    @Override
    public List<SysRoleEntity> listValid() {
        return this.lambdaQuery()
                .eq(SysRoleEntity::getIsvalid, true)
                .list();
    }

    @Override
    public void deleteByWorkSpaceId(Long workSpaceId) {
        LambdaQueryWrapper<SysRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleEntity::getWorkSpaceId, workSpaceId);
        this.remove(wrapper);
    }

    @Override
    public SysRoleEntity findByRoleNameAndWorkspaceId(String roleName, Long workspaceId) {
        LambdaQueryWrapper<SysRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleEntity::getWorkSpaceId, workspaceId)
                .eq(SysRoleEntity::getRoleName, roleName);
        return this.getOne(wrapper);
    }
}