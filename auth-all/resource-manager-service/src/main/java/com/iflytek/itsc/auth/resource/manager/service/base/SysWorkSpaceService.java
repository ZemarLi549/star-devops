package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysWorkSpaceEntity;

import java.util.List;
import java.util.Map;

public interface SysWorkSpaceService extends MPJBaseService<SysWorkSpaceEntity> {

    /**
     * 查询所有有效工作组
     */
    List<SysWorkSpaceEntity> listValid();

    /**
     * 查询所有有效工作组
     */
    List<SysWorkSpaceEntity> listValidByUserId(Long userId);

    Map<Long, Long> listSpaceUserCount();

    Map<Long, Long> listSpaceRoleCount();
}