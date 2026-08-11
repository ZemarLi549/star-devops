package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceSpaceApplication;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysApplicationEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysApplicationMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysApplicationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@Service
public class SysApplicationServiceImpl extends MPJBaseServiceImpl<SysApplicationMapper, SysApplicationEntity> implements SysApplicationService {
    @Override
    public void deleteByWorkSpaceId(Long workSpaceId) {
        LambdaQueryWrapper<SysApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysApplicationEntity::getWorkSpaceId, workSpaceId);
        this.remove(wrapper);
    }

    @Override
    public void deleteByDataGroupIds(List<Long> dataGroupIds) {
        LambdaQueryWrapper<SysApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysApplicationEntity::getDataGroupId, dataGroupIds);
        this.remove(wrapper);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        LambdaQueryWrapper<SysApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysApplicationEntity::getApplicationId, ids);
        this.remove(wrapper);
    }

    @Override
    public List<ResourceSpaceApplication> findValidApplications(Long workspaceId) {
        MPJLambdaWrapper wrapper = new MPJLambdaWrapper<SysApplicationEntity>()
                .select(SysApplicationEntity::getApplicationName)
                .select(SysDataGroupEntity::getDataGroupName)
                .select(SysDataGroupEntity::getDataGroupToken)
                .select(SysApplicationEntity::getApplicationCode)
                .select(SysApplicationEntity::getModifyTime)
                .leftJoin(SysDataGroupEntity.class, SysDataGroupEntity::getDataGroupId, SysApplicationEntity::getDataGroupId)
                .eq(SysApplicationEntity::getWorkSpaceId, workspaceId)
                .eq(SysApplicationEntity::getIsvalid, true);

        List<ResourceSpaceApplication> resourceSpaceApplications = this.selectJoinList(ResourceSpaceApplication.class, wrapper);
        return resourceSpaceApplications;
    }

    @Override
    public List<SysApplicationEntity> listValid() {
        return this.lambdaQuery()
                .eq(SysApplicationEntity::getIsvalid, true)
                .list();
    }
}
