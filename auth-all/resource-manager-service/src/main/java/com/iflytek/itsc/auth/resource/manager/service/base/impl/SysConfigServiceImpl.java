package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import com.github.yulichang.base.MPJBaseServiceImpl;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.service.base.SysConfigService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysConfigEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 公共配置
 */
@Service
public class SysConfigServiceImpl extends MPJBaseServiceImpl<SysConfigMapper, SysConfigEntity> implements SysConfigService {

    /**
     * 查询子平台
     */
    @Override
    public List<SysConfigEntity> listSysConfigEntityByPropertyType(String propertyType) {
        return this.lambdaQuery()
                .eq(SysConfigEntity::getPropertyType, propertyType)
                .eq(SysConfigEntity::getIsvalid, true)
                .orderByAsc(SysConfigEntity::getSortNum)
                .list();
    }

    @Override
    public SysConfigEntity listSysConfigEntityByPropertyTypeAndKey(String propertyType, String propertyKey) {
        return this.lambdaQuery()
                .eq(SysConfigEntity::getPropertyType, propertyType)
                .eq(SysConfigEntity::getPropertyKey, propertyKey)
                .eq(SysConfigEntity::getIsvalid, true)
                .orderByAsc(SysConfigEntity::getSortNum)
                .one();
    }

    /**
     * 查询子平台编码
     */
    @Override
    public Set<String> listModuleKeys(String propertyType) {
        List<SysConfigEntity> list = this.lambdaQuery()
                .eq(SysConfigEntity::getPropertyType, propertyType)
                .eq(SysConfigEntity::getIsvalid, true)
                .list();
        return list.stream().map(SysConfigEntity::getPropertyKey).collect(Collectors.toSet());
    }

    /**
     * 查询默认密码
     */
    @Override
    public String getDefaultPwd() {
        SysConfigEntity defaultPwd = this.lambdaQuery()
                .eq(SysConfigEntity::getPropertyType, Constant.USER_DATA)
                .eq(SysConfigEntity::getPropertyKey, Constant.DEFAULT_P_W_D)
                .eq(SysConfigEntity::getIsvalid, true)
                .one();
        return defaultPwd.getPropertyValue();
    }
}