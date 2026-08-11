package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDeployMetaInfoEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysDeployInfoMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysDeployInfoService;
import org.springframework.stereotype.Service;

/**
 * @Classname SysDeployInfoServiceImpl
 * @Description TODO
 * @Date 2024/10/17 14:22
 * @Created by wxqiu
 */
@Service
public class SysDeployInfoServiceImpl extends MPJBaseServiceImpl<SysDeployInfoMapper, SysDeployMetaInfoEntity> implements SysDeployInfoService {


    @Override
    public SysDeployMetaInfoEntity findByModuleName(String moduleName) {
        return this.lambdaQuery().eq(SysDeployMetaInfoEntity::getModuleName,moduleName).one();
    }

    @Override
    public void deleteByModuleName(String moduleName) {
        LambdaQueryWrapper<SysDeployMetaInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDeployMetaInfoEntity::getModuleName, moduleName);
        this.remove(wrapper);
    }
}
