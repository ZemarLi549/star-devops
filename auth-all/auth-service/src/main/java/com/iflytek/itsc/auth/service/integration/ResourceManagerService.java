package com.iflytek.itsc.auth.service.integration;

import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.domain.dto.*;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.service.base.SysShortcutMenuService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerPermissionService;
import com.iflytek.itsc.auth.service.domain.dto.*;
import com.iflytek.itsc.auth.service.integration.dto.InsertUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname ResourceManagerService
 * @Description
 * @Date 2023/12/19 16:05
 * @Created by wxqiu
 */

@Component
@Slf4j
public class ResourceManagerService {
    @Autowired
    private InnerPermissionService innerPermissionService;

    @Autowired
    private SysShortcutMenuService shortcutMenuService;


    /**
     * 查询单个用户权限资源
     */
    public ResourceUser getResourceUser(UserPermissionRequest userPermissionRequest) {
        try {
            return innerPermissionService.getUserPermission(userPermissionRequest.getAccount());
        } catch (Exception e) {
            log.error("getResourceUser method error", e);
            return null;

        }
    }

    /**
     * 获取当前用户当前工作空间下的菜单信息
     */

    public List<ResourceUserMenuDetail> listMenus(Long userId, Long workspaceId, String deployMode) {
        return innerPermissionService.listMenus(userId, workspaceId, deployMode);
    }

    /**
     * 获取当前用户的工作空间信息
     */
    public List<ResourceUserWorkSpace> listWorkspaces(Long userId) {
        return innerPermissionService.listWorkspaces(userId);

    }


    /**
     * 查询当前工作空间的所有数据单元信息
     */
    public List<SysDataGroupEntity> findDataGroups(Long workspaceId) {
        return innerPermissionService.findDataGroups(workspaceId);

    }

    /**
     * 获取当前工作空间下的应用列表信息
     */
    public List<ResourceSpaceApplication> findApplications(Long workspaceId) {
        return innerPermissionService.findApplications(workspaceId);

    }


    /**
     * 更新用户为登录过
     */
    public void updateLogged(Long userId) {
        innerPermissionService.updateLogged(userId);


    }

    /**
     * 更新用户登录时间
     */
    public void updateLoginTime(Long userId) {
        innerPermissionService.updateLoginTime(userId);


    }

    public void updateUser(UserUpdateRequest userUpdateRequest) {
        innerPermissionService.updateUser(userUpdateRequest.toSysUserForm(userUpdateRequest));


    }

    public void updatePassword(PasswordUpdateRequest passwordUpdateRequest) {
        innerPermissionService.updatePassword(passwordUpdateRequest.toSysUserPageParam(passwordUpdateRequest));


    }

    public ResourceUser insertUser(InsertUserInfo insertUserInfo) {
        return innerPermissionService.insertUser(insertUserInfo.toSysUserForm(insertUserInfo));


    }

    public void updateGuideState(UpdateGuideStateRequest updateGuideStateRequest) {
        innerPermissionService.updateGuideState(UserInfoContext.getUserId(), updateGuideStateRequest.getGuideState());
    }

    public void validateIntegrality() {
        Long userId = UserInfoContext.getUserId();
        innerPermissionService.validateIntegrality(userId);
    }

    public void initCipherData() {
        innerPermissionService.initCipherData();
    }

    public List<FirstLevelMenu> findShortcutMenu() {
        Long workspaceId = UserInfoContext.getWorkspaceId();
        String account = UserInfoContext.getAccount();
        List<ShortcutMenuDTO> shortcutMenuDTOS = shortcutMenuService.findByAccountAndWorkspaceId(account, workspaceId);

        return shortcutMenuDTOS.stream().map(_menu -> {
            FirstLevelMenu firstLevelMenu = new FirstLevelMenu();
            firstLevelMenu.setMenuId(String.valueOf(_menu.getMenuId()));
            firstLevelMenu.setMenuName(_menu.getMenuName());
            firstLevelMenu.setMenuPath(_menu.getMenuPath());
            return firstLevelMenu;
        }).collect(Collectors.toList());
    }

    public void saveShortcutMenu(FirstLevelMenu firstLevelMenu) {
        shortcutMenuService.save(firstLevelMenu.toSysShortcutMenuEntity());
    }

    public void deleteShortcutMenu(String menuId) {
        Long workspaceId = UserInfoContext.getWorkspaceId();
        String account = UserInfoContext.getAccount();
        shortcutMenuService.deleteByWorkspaceIdAndAccountAndMenuId(workspaceId, account, Long.valueOf(menuId));
    }

    /**
     * 查询单个用户权限资源
     */
    public DeployInfoDTO listDeployInfo() {
        return innerPermissionService.listDeployInfo();

    }

    /**
     * 查询单个用户权限资源
     */
    public void saveDeployMetaInfoAndMenu(String moduleName) {
        innerPermissionService.saveDeployMetaInfoAndMenu(moduleName);

    }


    /**
     * 查询单个用户权限资源
     */
    public void deleteDeployMetaInfoAndMenu(String moduleName) {
        innerPermissionService.deleteDeployMetaInfoAndMenu(moduleName);

    }


}
