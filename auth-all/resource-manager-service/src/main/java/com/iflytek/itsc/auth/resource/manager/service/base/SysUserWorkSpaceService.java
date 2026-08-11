package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SysUserWorkSpaceService extends MPJBaseService<SysUserWorkSpaceEntity> {

    void insert(Long Long, Long workSpaceId);

    void deleteByWorkSpaceId(Long workGroupId);

    void deleteByUserIds(Collection<Long> userIds);

    List<SysUserWorkSpaceEntity> listByWorkSpaceIds(Collection<Long> ids);

    void deleteBy(Long userId, Long workSpaceId);

    void deleteBySpaceIdUserIds(Long workSpaceId, Collection<Long> userIds);

    void deleteByUserIdSpaceIds(Long userId, Collection<Long> spaceIds);

    Long countByUser(Long userId);

    Set<Long> listSpaceIdByUserId(Long userId);

    List<SysUserWorkSpaceEntity> listByUserIds(Collection<Long> userIds);
}