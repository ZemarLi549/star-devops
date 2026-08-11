package com.iflytek.itsc.auth.resource.manager.service.base.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.base.MPJBaseServiceImpl;
import com.github.yulichang.toolkit.JoinWrappers;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysMenuBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysConfigEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleMenuEntity;
import com.iflytek.itsc.auth.resource.manager.mapper.SysMenuMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends MPJBaseServiceImpl<SysMenuMapper, SysMenuEntity> implements SysMenuService {
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuResourceService sysMenuResourceService;
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 查询所有菜单
     */
    @Override
    public List<SysMenuEntity> listValid() {
        return this.lambdaQuery()
                .eq(SysMenuEntity::getIsvalid, true)
                .orderByAsc(SysMenuEntity::getMenuId)
                .list();
    }

    @Override
    public List<SysMenuEntity> listValidByRoleId(Long roleId) {
        MPJLambdaWrapper wrapper = new MPJLambdaWrapper<SysMenuEntity>()
                .selectAll(SysMenuEntity.class)
                .leftJoin(SysRoleMenuEntity.class, SysRoleMenuEntity::getMenuId, SysMenuEntity::getMenuId)
                .eq(SysRoleMenuEntity::getRoleId, roleId)
                .eq(SysMenuEntity::getIsvalid, true)
                .orderByAsc(SysMenuEntity::getMenuId);

        return this.selectJoinList(SysMenuEntity.class, wrapper);
    }

    @Override
    public List<SysMenuEntity> findByMenuNo(List<String> menuNos) {
        return this.lambdaQuery()
                .eq(SysMenuEntity::getIsvalid, true)
                .in(SysMenuEntity::getMenuNo, menuNos)
                .list();
    }

    /**
     * 查询当前最大的菜单id
     */
    @Override
    public Long getMaxMenuId() {
        Long maxMenuId = JoinWrappers.lambda(SysMenuEntity.class)
                .selectMax(SysMenuEntity::getMenuId)
                .one(Long.class);
        return maxMenuId == null ? 0L : maxMenuId;
    }

    @Override
    public void deleteByMenuIds(Collection<Long> menuIds) {
        LambdaQueryWrapper<SysMenuEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenuEntity::getMenuId, menuIds);
        this.remove(wrapper);
    }

    @Override
    public List<SysMenuEntity> listByModule(String module) {
        return this.lambdaQuery()
                .eq(SysMenuEntity::getIsvalid, true)
                .eq(SysMenuEntity::getModuleType, module)
                .orderByAsc(SysMenuEntity::getMenuId)
                .list();
    }

    /**
     * 查询登录用户权限内的菜单、接口、按钮树
     */
    @Override
    public List<MenuTreeDTO> getAuthTree(Long userId, Long workSpaceId) {
        // 是否超管
        boolean isSuperAdmin = sysUserService.isSuperAdmin(userId);
        // 服务模块
        List<SysConfigEntity> moduleList = sysConfigService.listSysConfigEntityByPropertyType(Constant.MODULE_TYPE);

        List<SysMenuEntity> menuEntities = null;
        if (isSuperAdmin) {
            menuEntities = this.listValid();
        } else {
            List<Long> roleIds = sysUserRoleService.listRoleIdsBy(userId, workSpaceId);
            if (CollectionUtil.isNotEmpty(roleIds)) {
                List<Long> menuIds = sysRoleMenuService.listMenuIdsBy(roleIds);
                if (CollectionUtil.isNotEmpty(menuIds)) {
                    menuEntities = this.listByIds(menuIds);
                }
            }
        }
        if (CollectionUtil.isEmpty(menuEntities)) {
            return new ArrayList<>();
        }

        return SysMenuBuilder.buildMenuResourceTree(true, menuEntities, moduleList);
    }

    @Override
    public Set<String> listDefaultIds() {
        List<SysMenuEntity> list = this.lambdaQuery()
                .eq(SysMenuEntity::getIsvalid, true)
                .eq(SysMenuEntity::getIsdefault, true)
                .select(SysMenuEntity::getMenuId)
                .list();
        return list.stream().map(s -> String.valueOf(s.getMenuId())).collect(Collectors.toSet());
    }
}