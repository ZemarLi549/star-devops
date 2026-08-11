package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysDataGroupMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysDataGroupService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@Service
public class SysDataGroupServiceImpl extends MPJBaseServiceImpl<SysDataGroupMapper, SysDataGroupEntity> implements SysDataGroupService {

    @Override
    public void deleteByWorkSpaceId(Long workSpaceId) {
        LambdaQueryWrapper<SysDataGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDataGroupEntity::getWorkSpaceId, workSpaceId);
        this.remove(wrapper);
    }

    @Override
    public List<SysDataGroupEntity> listValid() {
        return this.lambdaQuery()
                .eq(SysDataGroupEntity::getIsvalid, true)
                .list();
    }

    @Override
    public List<SysDataGroupEntity> listElementValidByWorkspaceId(Long workspaceId) {
        // 所有业务组/数据单元对象
        List<SysDataGroupEntity> allDG = this.lambdaQuery()
                .select(SysDataGroupEntity::getDataGroupId, SysDataGroupEntity::getDataGroupToken, SysDataGroupEntity::getDataGroupName)
                .eq(SysDataGroupEntity::getIsvalid, true)
                .eq(SysDataGroupEntity::getWorkSpaceId, workspaceId)
                .eq(SysDataGroupEntity::getIselement, true)
                .orderByAsc(SysDataGroupEntity::getSortNum)
                .list();
        return allDG;
    }

    @Override
    public void deleteByIds(Collection<Long> dataGroupIds) {
        LambdaQueryWrapper<SysDataGroupEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysDataGroupEntity::getDataGroupId, dataGroupIds);
        this.remove(wrapper);
    }
}
