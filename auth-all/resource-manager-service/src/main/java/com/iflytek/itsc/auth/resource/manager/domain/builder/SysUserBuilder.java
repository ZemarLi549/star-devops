package com.iflytek.itsc.auth.resource.manager.domain.builder;

import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;

import java.util.Date;
import java.util.Locale;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
public class SysUserBuilder {

    public static SysUserEntity buildInsertEntity(SysUserForm form) {
        SysUserEntity entity = new SysUserEntity();
        entity.setAccount(form.getAccount());
        entity.setNickName(form.getNickName());
        entity.setEmail(form.getEmail());
        entity.setPhone(form.getPhone());
        entity.setFeishuUserId(form.getFeishuUserId());
        entity.setItWorkbenchUserId(form.getItWorkbenchUserId());
        entity.setIdentitySource(normalizeIdentitySource(form.getIdentitySource()));
        entity.setLdapAccount(form.getLdapAccount());
        entity.setIsactive(true);
        entity.setModifyTime(new Date());
        entity.setCreateTime(new Date());
        return entity;
    }

    public static SysUserEntity buildUpdateEntity(SysUserForm form) {
        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(form.getUserId());
        entity.setAccount(form.getAccount());
        entity.setNickName(form.getNickName());
        entity.setEmail(form.getEmail());
        entity.setPhone(form.getPhone());
        entity.setFeishuUserId(form.getFeishuUserId());
        entity.setItWorkbenchUserId(form.getItWorkbenchUserId());
        entity.setIdentitySource(normalizeIdentitySource(form.getIdentitySource()));
        entity.setLdapAccount(form.getLdapAccount());
        entity.setModifyTime(new Date());
        return entity;
    }

    private static String normalizeIdentitySource(String identitySource) {
        if (identitySource == null || identitySource.trim().isEmpty()) {
            return "LOCAL";
        }
        return identitySource.trim().toUpperCase(Locale.ROOT);
    }
}
