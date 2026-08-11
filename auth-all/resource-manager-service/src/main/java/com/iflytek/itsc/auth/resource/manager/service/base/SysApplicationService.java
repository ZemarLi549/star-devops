package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceSpaceApplication;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysApplicationEntity;

import java.util.List;

public interface SysApplicationService extends MPJBaseService<SysApplicationEntity> {
    List<SysApplicationEntity> listValid();

    void deleteByWorkSpaceId(Long workSpaceId);

    void deleteByDataGroupIds(List<Long> dataGroupIds);

    void deleteByIds(List<Long> ids);

    List<ResourceSpaceApplication> findValidApplications(Long workspaceId);
}
