package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;

import java.util.Collection;
import java.util.List;

public interface SysDataGroupService extends MPJBaseService<SysDataGroupEntity> {

    void deleteByWorkSpaceId(Long workSpaceId);

    List<SysDataGroupEntity> listValid();

    List<SysDataGroupEntity> listElementValidByWorkspaceId(Long workspaceId);

    void deleteByIds(Collection<Long> dataGroupIds);
}
