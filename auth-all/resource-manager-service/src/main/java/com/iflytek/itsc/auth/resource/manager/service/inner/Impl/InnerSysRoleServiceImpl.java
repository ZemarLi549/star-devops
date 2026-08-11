package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysMenuBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysRoleBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.RoleDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.*;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysRoleForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.RoleQueryParam;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysRoleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc
 **/
@Service
public class InnerSysRoleServiceImpl implements InnerSysRoleService {
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysMenuResourceService sysMenuResourceService;
    @Autowired
    private SysWorkSpaceService sysWorkSpaceService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private UserCacheDeleteUtil userCacheDeleteUtil;

    @Override
    @Transactional
    public void insert(SysRoleForm form) {
        // 合法校验
        form.setRoleId(null);
        this.validateLegal(form);

        // 保存角色，并获取角色id
        SysRoleEntity roleEntity = SysRoleBuilder.buildInsertEntity(form);
        sysRoleService.save(roleEntity);
        roleEntity = sysRoleService.findByRoleNameAndWorkspaceId(form.getRoleName(), form.getWorkSpaceId());

        // 待保存的菜单、资源
        List<SysRoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(form.getMenuIds())) {
            // 根据所有菜单、资源过滤表单中的菜单id
            List<SysMenuEntity> menuEntityList = sysMenuService.listValid();
            Set<String> allMenuIdSet = menuEntityList.stream().map(s -> String.valueOf(s.getMenuId())).collect(Collectors.toSet());
            for (String id : form.getMenuIds()) {
                if (allMenuIdSet.contains(id)) {
                    SysRoleMenuEntity entity = new SysRoleMenuEntity();
                    entity.setRoleId(roleEntity.getRoleId());
                    entity.setMenuId(Long.valueOf(id));
                    roleMenuEntityList.add(entity);
                }
            }
        }
        // 保存角色菜单
        if (CollectionUtil.isNotEmpty(roleMenuEntityList)) {
            sysRoleMenuService.saveBatch(roleMenuEntityList);
        }
    }

    @Override
    @Transactional
    public void update(SysRoleForm form) {
        // 合法校验
        this.validateLegal(form);

        // 待保存角色
        SysRoleEntity roleEntity = SysRoleBuilder.buildUpdateEntity(form);
        // 待保存的菜单、资源
        List<SysRoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(form.getMenuIds())) {
            // 根据所有菜单、资源过滤表单中的菜单id
            List<SysMenuEntity> menuEntityList = sysMenuService.listValid();
            List<SysMenuResourceEntity> menuResourceEntityList = sysMenuResourceService.list();
            Set<String> allMenuIdSet = menuEntityList.stream().map(s -> String.valueOf(s.getMenuId())).collect(Collectors.toSet());
            for (String id : form.getMenuIds()) {
                if (allMenuIdSet.contains(id)) {
                    SysRoleMenuEntity entity = new SysRoleMenuEntity();
                    entity.setRoleId(roleEntity.getRoleId());
                    entity.setMenuId(Long.valueOf(id));
                    roleMenuEntityList.add(entity);
                }
            }
        }

        // 更新角色
        sysRoleService.updateById(roleEntity);
        // 保存角色菜单
        sysRoleMenuService.deleteByRoleId(roleEntity.getRoleId());
        if (CollectionUtil.isNotEmpty(roleMenuEntityList)) {
            sysRoleMenuService.saveBatch(roleMenuEntityList);
        }
        //删除和这个角色绑定的用户缓存信息
        List<String> accounts = sysUserService.listAccountsByRoleId(roleEntity.getRoleId());
        //用户关联的工作空间变更后需要删除缓存信息
        userCacheDeleteUtil.deleteUserCache(accounts);

    }

    private void validateLegal(SysRoleForm form) {
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (workSpaceEntity == null) {
            throw new AuthBizException("工作空间不存在");
        }
        if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        if (form.getRoleId() != null) {
            SysRoleEntity roleEntity = sysRoleService.getById(form.getRoleId());
            if (roleEntity == null) {
                throw new AuthBizException("角色不存在");
            }
            if (!roleEntity.getWorkSpaceId().equals(form.getWorkSpaceId())) {
                throw new AuthBizException("工作空间不可变更");
            }
            if (roleEntity.getIssuperadmin() || roleEntity.getIsdefault()) {
                throw new AuthBizException("内置角色不可编辑");
            }
        }

        List<SysRoleEntity> list = sysRoleService.lambdaQuery()
                .eq(SysRoleEntity::getIsvalid, true)
                .eq(SysRoleEntity::getRoleName, form.getRoleName())
                .and(s -> s.eq(SysRoleEntity::getIsdefault, true).or().eq(SysRoleEntity::getWorkSpaceId, form.getWorkSpaceId()))
                .select(SysRoleEntity::getRoleId)
                .list();
        if (CollectionUtil.isNotEmpty(list) && !list.get(0).getRoleId().equals(form.getRoleId())) {
            throw new AuthBizException("角色名称已存在");
        }
    }

    @Override
    @Transactional
    public void delete(SysRoleForm form) {
        SysRoleEntity roleEntity = sysRoleService.getById(form.getRoleId());
        if (roleEntity == null) {
            throw new AuthBizException("角色不存在");
        }
        if (roleEntity.getIssuperadmin() || roleEntity.getIsdefault()) {
            throw new AuthBizException("内置角色不可删除");
        }
        if (!UserInfoContext.isWorkSpaceRelated(roleEntity.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        List<SysUserRoleEntity> sysUserRoleEntities = sysUserRoleService.listByRoleId(form.getRoleId());
        if (CollectionUtil.isNotEmpty(sysUserRoleEntities)) {
            throw new AuthBizException("删除角色之前，请先移除该角色下的所有用户");
        }
        // 删除角色菜单关系
        sysRoleMenuService.deleteByRoleId(form.getRoleId());
        // 删除角色
        sysRoleService.removeById(form.getRoleId());
    }

    /**
     * 分页查询
     */
    @Override
    public Page<RoleDTO> page(RoleQueryParam param) {
        if (param.getWorkSpaceId() == null) {
            throw new AuthBizException("工作空间不能为空");
        }
        if (!UserInfoContext.isWorkSpaceRelated(param.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }

        // 分页查询
        LambdaQueryWrapper<SysRoleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(s -> s.eq(SysRoleEntity::getIsdefault, true).or().eq(SysRoleEntity::getWorkSpaceId, param.getWorkSpaceId()));
        wrapper.like(StringUtils.isNotBlank(param.getRoleName()), SysRoleEntity::getRoleName, param.getRoleName());
        wrapper.orderByDesc(SysRoleEntity::getIsdefault);
        wrapper.orderByAsc(SysRoleEntity::getCreateTime);
        Page<SysRoleEntity> queryPage = new Page<>(param.getPageNo(), param.getPageSize());
        queryPage = sysRoleService.page(queryPage, wrapper);

        // 查询用户数量
        Map<Long, Long> roleId2UserCountMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(queryPage.getRecords())) {
            List<Long> roleIds = queryPage.getRecords().stream().map(SysRoleEntity::getRoleId).collect(Collectors.toList());
            List<SysUserRoleEntity> userRoleList = sysUserRoleService.listBy(param.getWorkSpaceId(), roleIds);
            roleId2UserCountMap = userRoleList.stream()
                    .collect(Collectors.groupingBy(SysUserRoleEntity::getRoleId,
                            Collectors.mapping(SysUserRoleEntity::getUserId, Collectors.counting())));
        }

        // 转换对象
        Page<RoleDTO> page = new Page<>();
        BeanUtil.copyProperties(queryPage, page);
        List<RoleDTO> dtoList = new ArrayList<>(queryPage.getRecords().size());
        for (SysRoleEntity record : queryPage.getRecords()) {
            RoleDTO dto = SysRoleBuilder.buildPageDTO(record);
            dto.setUserCount(roleId2UserCountMap.getOrDefault(dto.getRoleId(), 0L));
            dtoList.add(dto);
        }
        page.setRecords(dtoList);
        return page;
    }

    /**
     * 查询参考角色
     */
    @Override
    public List<KeyValueDTO> listTemplate() {
        List<SysRoleEntity> list = sysRoleService.lambdaQuery()
                .eq(SysRoleEntity::getIsvalid, true)
                .eq(SysRoleEntity::getIsdefault, true)
                .list();
        return list.stream().map(s -> new KeyValueDTO(String.valueOf(s.getRoleId()), s.getRoleName())).collect(Collectors.toList());
    }

    /**
     * 查询菜单详情
     */
    @Override
    public RoleDTO getDetail(Long roleId) {
        SysRoleEntity roleEntity = sysRoleService.getById(roleId);
        if (roleEntity == null) throw new AuthBizException("角色不存在");
        if (!roleEntity.getIsdefault() && !UserInfoContext.isWorkSpaceRelated(roleEntity.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }

        RoleDTO dto = SysRoleBuilder.buildPageDTO(roleEntity);

        // 查询角色菜单树
        List<Long> menuIds = sysRoleMenuService.listMenuIdsBy(roleId);
        if (CollectionUtil.isEmpty(menuIds)) {
            dto.setMenuList(new ArrayList<>());
            return dto;
        }
        List<SysMenuEntity> menuEntities = sysMenuService.listByIds(menuIds);
        //根据主键进行排序
        menuEntities.sort((o1, o2) -> {
            return o1.getMenuId() > o2.getMenuId() ? 1 : -1;
        });
        List<SysMenuResourceEntity> menuResourceEntities = new ArrayList<>();
        List<SysConfigEntity> moduleList = sysConfigService.listSysConfigEntityByPropertyType(Constant.MODULE_TYPE);
        List<MenuTreeDTO> list = SysMenuBuilder.buildMenuResourceTree(true, menuEntities, moduleList);

        dto.setMenuList(list);
        return dto;
    }

    /**
     * 查询权限内菜单树，如果有角色，则标记角色菜单选中状态
     */
    @Override
    public List<MenuTreeDTO> getAuthMenuTree(Long workSpaceId, Long roleId) {
        if (workSpaceId == null || !UserInfoContext.isWorkSpaceRelated(workSpaceId)) {
            throw new AuthBizException("无工作空间权限");
        }

        // 查询默认菜单
        Set<String> defaultMenuIds = sysMenuService.listDefaultIds();
        Set<String> selectIdSet = new HashSet<>(defaultMenuIds);
        // 查询角色绑定的菜单
        if (roleId != null) {
            SysRoleEntity roleEntity = sysRoleService.getById(roleId);
            if (roleEntity == null) throw new AuthBizException("角色不存在");
            if (!roleEntity.getIsdefault() && !roleEntity.getWorkSpaceId().equals(workSpaceId)) {
                throw new AuthBizException("角色与工作空间不匹配");
            }

            // 查询角色绑定的菜单、资源
            List<Long> menuIds = sysRoleMenuService.listMenuIdsBy(roleId);
            for (Long menuId : menuIds) {
                selectIdSet.add(String.valueOf(menuId));
            }
        }

        // 查询权限内的菜单、资源树
        List<MenuTreeDTO> authMenuTree = sysMenuService.getAuthTree(UserInfoContext.getUserId(), workSpaceId);
        // 在权限菜单树上，标记是否选中,如果下级选中了，需将自身也选中
        if (CollectionUtil.isNotEmpty(authMenuTree)) {
            SysMenuBuilder.markSelect(authMenuTree, selectIdSet, defaultMenuIds);
        }
        return authMenuTree;
    }

    @Override
    public List<KeyValueDTO> listSpaceTotal(Long workSpaceId) {
        if (workSpaceId == null || !UserInfoContext.isWorkSpaceRelated(workSpaceId)) {
            throw new AuthBizException("无工作空间权限");
        }

        List<SysRoleEntity> list = sysRoleService.lambdaQuery()
                .eq(SysRoleEntity::getIsvalid, true)
                .and(s -> s.eq(SysRoleEntity::getIsdefault, true).or().eq(SysRoleEntity::getWorkSpaceId, workSpaceId))
                .orderByDesc(SysRoleEntity::getIsdefault)
                .orderByDesc(SysRoleEntity::getModifyTime)
                .select(SysRoleEntity::getRoleId, SysRoleEntity::getRoleName)
                .list();
        return list.stream().map(s -> new KeyValueDTO(String.valueOf(s.getRoleId()), s.getRoleName())).collect(Collectors.toList());
    }
}
