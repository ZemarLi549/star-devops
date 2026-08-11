package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDeployMetaInfoEntity;

public interface SysDeployInfoService extends MPJBaseService<SysDeployMetaInfoEntity> {




    SysDeployMetaInfoEntity findByModuleName(String moduleName);


    void deleteByModuleName(String moduleName);

}