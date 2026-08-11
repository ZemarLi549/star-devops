package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysConfigEntity;

import java.util.List;
import java.util.Set;

public interface SysConfigService extends MPJBaseService<SysConfigEntity> {

    /**
     * 查询子平台
     */
    List<SysConfigEntity> listSysConfigEntityByPropertyType(String propertyType);

    SysConfigEntity listSysConfigEntityByPropertyTypeAndKey(String propertyType, String propertyKey);

    /**
     * 查询子平台标识
     */
    Set<String> listModuleKeys(String propertyType);

    /**
     * 获取用户默认密码
     * @return
     */
    String getDefaultPwd();
}