package com.iflytek.itsc.auth.resource.manager.domain.builder;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceSaveForm;

import java.util.Date;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
public class SysWorkSpaceBuilder {

    public static SysWorkSpaceEntity buildInsertEntity(SysWorkSpaceSaveForm form) {
        SysWorkSpaceEntity entity = new SysWorkSpaceEntity();
        entity.setWorkSpaceName(form.getWorkSpaceName());
        entity.setRemark(form.getRemark());
        entity.setCreateUser(UserInfoContext.getUserId());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }

    public static SysWorkSpaceEntity buildUpdateEntity(SysWorkSpaceSaveForm form) {
        SysWorkSpaceEntity entity = new SysWorkSpaceEntity();
        entity.setWorkSpaceId(form.getWorkSpaceId());
        entity.setWorkSpaceName(form.getWorkSpaceName());
        entity.setRemark(form.getRemark());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setModifyTime(new Date());
        return entity;
    }
}
