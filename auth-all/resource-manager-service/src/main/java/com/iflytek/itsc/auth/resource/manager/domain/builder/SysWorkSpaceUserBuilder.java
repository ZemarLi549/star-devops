package com.iflytek.itsc.auth.resource.manager.domain.builder;

import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceUserForm;

import java.util.Date;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc
 **/
public class SysWorkSpaceUserBuilder {

    public static SysUserWorkSpaceEntity buildUserWorkSpaceEntity(SysWorkSpaceUserForm form) {
        SysUserWorkSpaceEntity entity = new SysUserWorkSpaceEntity();
        entity.setWorkSpaceId(form.getWorkSpaceId());
        entity.setUserId(form.getUserId());
        entity.setCreateTime(new Date());
        return entity;
    }

    public static SysUserWorkSpaceEntity buildUserWorkSpaceEntity(Long userId, Long spaceId) {
        SysUserWorkSpaceEntity entity = new SysUserWorkSpaceEntity();
        entity.setWorkSpaceId(spaceId);
        entity.setUserId(userId);
        entity.setCreateTime(new Date());
        return entity;
    }

    public static SysUserRoleEntity buildUserRoleEntity(SysWorkSpaceUserForm form) {
        SysUserRoleEntity entity = new SysUserRoleEntity();
        entity.setWorkSpaceId(form.getWorkSpaceId());
        entity.setUserId(form.getUserId());
        entity.setRoleId(form.getRoleId());
        entity.setCreateTime(new Date());
        return entity;
    }
}
