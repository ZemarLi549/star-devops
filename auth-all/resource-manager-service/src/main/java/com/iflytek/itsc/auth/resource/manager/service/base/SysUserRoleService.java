package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserRoleEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SysUserRoleService extends MPJBaseService<SysUserRoleEntity> {

    void deleteByUserIds(Collection<Long> userIds);

    void insert(Long userId, Long roleId, Long workSpaceId);

    List<Long> listRoleIdsBy(Long userId, Long workSpaceId);

    List<SysUserRoleEntity> listByRoleId(Long roleId);

    void deleteByWorkSpaceId(Long workSpaceId);

    void deleteBy(Long userId, Long workSpaceId);

    void deleteByUserIdSpaceIds(Long userId, Collection<Long> workSpaceId);

    void deleteBySpaceIdUserIds(Long workSpaceId, Collection<Long> userIds);

    List<SysUserRoleEntity> listBy(Long workSpaceId, List<Long> roleIds);

    Map<Long, String> listSpaceUserRoleName(Long userId);

    List<SysUserRoleEntity> listByUserIds(Collection<Long> userIds);

    List<SysUserRoleEntity> listByUserId(Long userId);

    Long getRoleIdByUserIdAndWorkspaceId(Long userId, Long workspaceId);
}