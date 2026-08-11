package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;

import java.util.List;

public interface SysRoleService extends MPJBaseService<SysRoleEntity> {

    List<SysRoleEntity> listValid();

    void deleteByWorkSpaceId(Long workSpaceId);

    SysRoleEntity findByRoleNameAndWorkspaceId(String roleName, Long workspaceId);
}