package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.domain.entity.*;
import com.iflytek.itsc.auth.resource.manager.mapper.SysUserMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.SysRoleService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserRoleService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserWorkSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("sysUserService")
public class SysUserServiceImpl extends MPJBaseServiceImpl<SysUserMapper, SysUserEntity> implements SysUserService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserWorkSpaceService sysUserWorkSpaceService;

    /**
     * 查询所有有效用户
     */
    @Override
    public List<SysUserEntity> listValid() {
        return this.lambdaQuery()
                .eq(SysUserEntity::getIsvalid, true)
                .list();
    }

    /**
     * 是否超管
     */
    @Override
    public boolean isSuperAdmin(Long userId) {
        List<SysRoleEntity> list = sysRoleService.lambdaQuery()
                .eq(SysRoleEntity::getIssuperadmin, true)
                .list();
        if (CollectionUtil.isEmpty(list)) return false;

        List<Long> roleIds = list.stream().map(SysRoleEntity::getRoleId).collect(Collectors.toList());
        Long count = sysUserRoleService.lambdaQuery()
                .eq(SysUserRoleEntity::getUserId, userId)
                .in(SysUserRoleEntity::getRoleId, roleIds)
                .count();
        return count > 0;
    }

    /**
     * 查询关联的空间，不算超管
     */
    @Override
    public Set<Long> listRelatedWorkSpaceIds(Long userId) {
        List<SysUserWorkSpaceEntity> list = sysUserWorkSpaceService.lambdaQuery()
                .eq(SysUserWorkSpaceEntity::getUserId, userId)
                .select(SysUserWorkSpaceEntity::getWorkSpaceId)
                .list();
        return list.stream().map(SysUserWorkSpaceEntity::getWorkSpaceId).collect(Collectors.toSet());
    }

    @Override
    public SysUserEntity getByAccount(String account) {
        return this.lambdaQuery()
                .eq(SysUserEntity::getIsvalid, true)
                .eq(SysUserEntity::getAccount, account)
                .last("limit 1")
                .one();
    }

    /**
     * 查询超管账户
     */
    @Override
    public List<String> listAdminAccount() {
        MPJLambdaWrapper<SysUserEntity> wrapper = new MPJLambdaWrapper<SysUserEntity>()
                .select(SysUserEntity::getAccount)
                .innerJoin(SysUserRoleEntity.class, SysUserRoleEntity::getUserId, SysUserEntity::getUserId)
                .innerJoin(SysRoleEntity.class, SysRoleEntity::getRoleId, SysUserRoleEntity::getRoleId)
                .eq(SysRoleEntity::getIssuperadmin, true)
                .groupBy(SysUserEntity::getAccount);
        List<SysUserEntity> userEntities = this.baseMapper.selectList(wrapper);
        return userEntities.stream().map(SysUserEntity::getAccount).collect(Collectors.toList());
    }

    /**
     * 根据角色查询关联用户，角色变化不影响超管
     */
    @Override
    public List<String> listAccountsByRoleId(Object... roleId) {
        MPJLambdaWrapper<SysUserEntity> wrapper = new MPJLambdaWrapper<SysUserEntity>()
                .select(SysUserEntity::getAccount)
                .innerJoin(SysUserRoleEntity.class, SysUserRoleEntity::getUserId, SysUserEntity::getUserId)
                .in(SysUserRoleEntity::getRoleId, roleId)
                .groupBy(SysUserEntity::getAccount);
        List<SysUserEntity> userEntities = this.baseMapper.selectList(wrapper);
        return userEntities.stream().map(SysUserEntity::getAccount).collect(Collectors.toList());
    }

    /**
     * 根据空间查询关联用户，加上超管，超管不一定有空间关系
     */
    @Override
    public List<String> listAccountsByWorkSpaceId(Object... workSpaceId) {
        // 超管账户
        List<String> accounts = this.listAdminAccount();

        MPJLambdaWrapper<SysUserEntity> wrapper = new MPJLambdaWrapper<SysUserEntity>()
                .select(SysUserEntity::getAccount)
                .innerJoin(SysUserWorkSpaceEntity.class, SysUserWorkSpaceEntity::getUserId, SysUserEntity::getUserId)
                .in(SysUserWorkSpaceEntity::getWorkSpaceId, workSpaceId)
                .groupBy(SysUserEntity::getAccount);
        List<SysUserEntity> userEntities = this.baseMapper.selectList(wrapper);
        for (SysUserEntity userEntity : userEntities) {
            accounts.add(userEntity.getAccount());
        }
        return accounts;
    }

    /**
     * 查询菜单关联用户
     */
    @Override
    public List<String> listAccountsByMenuId(Collection<Long> menuIds) {
        // 超管账户
        List<String> accounts = this.listAdminAccount();

        MPJLambdaWrapper<SysUserEntity> wrapper = new MPJLambdaWrapper<SysUserEntity>()
                .select(SysUserEntity::getAccount)
                .innerJoin(SysUserRoleEntity.class, SysUserRoleEntity::getUserId, SysUserEntity::getUserId)
                .innerJoin(SysRoleMenuEntity.class, SysRoleMenuEntity::getRoleId, SysUserRoleEntity::getRoleId)
                .in(SysRoleMenuEntity::getMenuId, menuIds)
                .groupBy(SysUserEntity::getAccount);
        List<SysUserEntity> userEntities = this.baseMapper.selectList(wrapper);
        for (SysUserEntity userEntity : userEntities) {
            accounts.add(userEntity.getAccount());
        }
        return accounts;
    }

    @Override
    public List<String> listAccountByUserIds(List<Long> userIds) {
        MPJLambdaWrapper<SysUserEntity> wrapper = new MPJLambdaWrapper<SysUserEntity>()
                .select(SysUserEntity::getAccount)
                .in(SysUserEntity::getUserId, userIds);
        List<SysUserEntity> userEntities = this.baseMapper.selectList(wrapper);
        return userEntities.stream().map(SysUserEntity::getAccount).collect(Collectors.toList());
    }
}