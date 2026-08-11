package com.iflytek.itsc.auth.resource.manager.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SysMenuMapper extends MPJBaseMapper<SysMenuEntity> {

    // 插入前需要开启 IDENTITY_INSERT 权限
    @Update("SET IDENTITY_INSERT sys_menu ON")
    void startIdentityInsert();

    // 插入后需要关闭 IDENTITY_INSERT 权限
    @Update("SET IDENTITY_INSERT sys_menu OFF")
    void endIdentityInsert();
}