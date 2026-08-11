package com.iflytek.itsc.auth.resource.manager.service.base;

import com.github.yulichang.base.MPJBaseService;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserEntity;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface SysUserService extends MPJBaseService<SysUserEntity> {

    /**
     * 查询所有有效用户
     */
    List<SysUserEntity> listValid();

    boolean isSuperAdmin(Long userId);

    /**
     * 查询关联的空间
     */
    Set<Long> listRelatedWorkSpaceIds(Long userId);

    /**
     * 根据账号查询
     */
    SysUserEntity getByAccount(String account);

    /**
     * 查询超管账户
     */
    List<String> listAdminAccount();

    /**
     * 根据角色查询关联用户
     */
    List<String> listAccountsByRoleId(Object... roleId);

    /**
     * 根据空间查询关联用户
     */
    List<String> listAccountsByWorkSpaceId(Object... workSpaceId);

    /**
     * 查询菜单关联用户
     */
    List<String> listAccountsByMenuId(Collection<Long> menuIds);

    List<String> listAccountByUserIds(List<Long> userId);
}