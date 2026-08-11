package com.iflytek.itsc.auth.resource.manager.domain.builder;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.utils.DozerBeanUtil;
import com.iflytek.itsc.auth.resource.manager.common.utils.UUIDUtil;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysApplicationEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationSaveForm;

import java.util.Date;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
public class SysApplicationBuilder {
    private static String PREFIX_APPID = "app_";

    public static SysApplicationEntity buildApplicationInsertEntity(SysApplicationSaveForm form) {
        SysApplicationEntity entity = DozerBeanUtil.convert(form,SysApplicationEntity.class);
        entity.setApplicationCode(PREFIX_APPID+ UUIDUtil.getRandomUUID().toLowerCase());
        entity.setCreateUser(UserInfoContext.getUserId());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }

    public static SysApplicationEntity buildApplicationUpdateEntity(SysApplicationSaveForm form) {
        SysApplicationEntity entity = DozerBeanUtil.convert(form,SysApplicationEntity.class);
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setModifyTime(new Date());
        return entity;
    }
}
