package com.iflytek.itsc.auth.resource.manager.domain.builder;

import cn.hutool.core.collection.CollectionUtil;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUserMenuDetail;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysConfigEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuResourceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysMenuForm;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
public class SysMenuBuilder {

    public static SysMenuEntity buildInsertEntity(SysMenuForm form) {
        SysMenuEntity entity = new SysMenuEntity();
        entity.setModuleType(form.getModuleType());
        entity.setParentId(form.getParentId());
        entity.setMenuName(form.getMenuName());
        entity.setMenuPath(form.getMenuPath());
        entity.setIcon(form.getIcon());
        entity.setSortNum(form.getSortNum());
        entity.setIsoutlink(form.getIsoutlink());
        entity.setIsgroup(form.getIsgroup());
        entity.setIsmenu(form.getIsmenu());
        entity.setCreateUser(UserInfoContext.getUserId());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }

    public static SysMenuEntity buildSaveBatchEntity(SysMenuForm form) {
        SysMenuEntity entity = new SysMenuEntity();
        entity.setMenuId(form.getMenuId());
        entity.setModuleType(form.getModuleType());
        entity.setParentId(form.getParentId());
        entity.setMenuName(form.getMenuName());
        entity.setMenuPath(form.getMenuPath());
        entity.setIcon(form.getIcon());
        entity.setSortNum(form.getSortNum());
        entity.setIsoutlink(form.getIsoutlink());
        entity.setIsgroup(form.getIsgroup());
        entity.setIsmenu(form.getIsmenu());
        entity.setMenuNo(form.getMenuNo());
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        return entity;
    }


    public static SysMenuEntity buildUpdateEntity(SysMenuForm form) {
        SysMenuEntity entity = new SysMenuEntity();
        entity.setModuleType(form.getModuleType());
        entity.setMenuId(form.getMenuId());
        entity.setParentId(form.getParentId());
        entity.setMenuName(form.getMenuName());
        entity.setMenuPath(form.getMenuPath());
        entity.setIcon(form.getIcon());
        entity.setSortNum(form.getSortNum());
        entity.setIsoutlink(form.getIsoutlink());
        entity.setIsgroup(form.getIsgroup());
        entity.setIsmenu(form.getIsmenu());
        entity.setModifyUser(UserInfoContext.getUserId());
        entity.setModifyTime(new Date());
        return entity;
    }

    /**
     * 转换菜单详情
     */
    public static SysMenuForm buildMenuDetail(SysMenuEntity menuEntity, List<SysMenuResourceEntity> menuResourceEntityList) {
        SysMenuForm menuForm = new SysMenuForm();
        menuForm.setMenuId(menuEntity.getMenuId());
        menuForm.setMenuName(menuEntity.getMenuName());
        menuForm.setMenuPath(menuEntity.getMenuPath());
        menuForm.setSortNum(menuEntity.getSortNum());
        menuForm.setParentId(menuEntity.getParentId());
        menuForm.setModuleType(menuEntity.getModuleType());
        menuForm.setIsoutlink(menuEntity.getIsoutlink());
        menuForm.setIsgroup(menuEntity.getIsgroup());
        menuForm.setIsmenu(menuEntity.getIsmenu());
        menuForm.setIcon(menuEntity.getIcon());
        menuForm.setApiList(new ArrayList<>());
        // 接口
        if (CollectionUtil.isNotEmpty(menuResourceEntityList)) {
            for (SysMenuResourceEntity menuResourceEntity : menuResourceEntityList) {
                SysMenuForm.ApiForm apiForm = new SysMenuForm.ApiForm();
                apiForm.setPath(menuResourceEntity.getPath());
                apiForm.setMethod(menuResourceEntity.getMethod());
                menuForm.getApiList().add(apiForm);
            }
        }
        return menuForm;
    }

    /**
     * 构造菜单树，按服务模块分级，包括接口、按钮节点
     */
    public static List<MenuTreeDTO> buildMenuResourceTree(boolean hasModuleElement, List<SysMenuEntity> menuList, List<SysConfigEntity> totalModuleList) {
        // 菜单降序
        menuList.sort((o1, o2) -> {
            if (o1.getSortNum().equals(o2.getSortNum())) {
                if (o1.getCreateTime().equals(o2.getCreateTime())) {
                    return 0;
                } else {
                    return o1.getCreateTime().before(o2.getCreateTime()) ? -1 : 1;
                }
            } else {
                return o2.getSortNum() - o1.getSortNum();
            }
        });

        // 将各排序元素转换对象
        Set<String> moduleSet = menuList.stream().map(SysMenuEntity::getModuleType).collect(Collectors.toSet());
        Map<String, MenuTreeDTO> menuId2NodeMap = new LinkedHashMap<>();
        // 如果要求存在服务模块节点，则服务模块做第一级
        if (hasModuleElement) {
            totalModuleList.sort(Comparator.comparingInt(SysConfigEntity::getSortNum));
            for (SysConfigEntity moduleEntity : totalModuleList) {
                if (!moduleSet.contains(moduleEntity.getPropertyKey())) {
                    continue;
                }
                MenuTreeDTO node = new MenuTreeDTO();
                node.setMenuId(moduleEntity.getPropertyKey());
                node.setIsgroup(false);
                node.setIsmenu(false);
                node.setMenuName(moduleEntity.getPropertyValue());
                node.setParentId(String.valueOf(Constant.DEFAULT_PARENT_ID));
                node.setIsapi(false);
                node.setIsbutton(false);
                node.setIsselect(false);
                node.setDisabled(false);
                node.setChildren(new ArrayList<>());
                menuId2NodeMap.put(node.getMenuId(), node);
            }
        }
        for (SysMenuEntity entity : menuList) {
            MenuTreeDTO node = new MenuTreeDTO();
            node.setMenuId(String.valueOf(entity.getMenuId()));
            node.setIsgroup(entity.getIsgroup());
            node.setIsmenu(entity.getIsmenu());
            node.setMenuName(entity.getMenuName());
            node.setIcon(entity.getIcon());
            // 多模块下，一级菜单父id取模块标识
            if (hasModuleElement && Constant.DEFAULT_PARENT_ID.equals(entity.getParentId())) {
                node.setParentId(entity.getModuleType());
            } else {
                node.setParentId(String.valueOf(entity.getParentId()));
            }
            node.setIsapi(false);
            node.setIsbutton(false);
            node.setIsselect(false);
            node.setDisabled(false);
            node.setChildren(new ArrayList<>());
            menuId2NodeMap.put(String.valueOf(entity.getMenuId()), node);
        }
        // 将子集放入父级children中
        for (Map.Entry<String, MenuTreeDTO> nodeEntry : menuId2NodeMap.entrySet()) {
            MenuTreeDTO node = nodeEntry.getValue();
            if (menuId2NodeMap.containsKey(node.getParentId())) {
                MenuTreeDTO parentNode = menuId2NodeMap.get(node.getParentId());
                parentNode.getChildren().add(node);
            }
        }

        // 只取一级节点
        return menuId2NodeMap.values().stream()
                .filter(s -> String.valueOf(Constant.DEFAULT_PARENT_ID).equals(s.getParentId()))
                .collect(Collectors.toList());
    }

    /**
     * 同步auth的菜单树
     */
    public static List<ResourceUserMenuDetail> buildPermissionTree(List<SysMenuEntity> menuList, List<SysConfigEntity> totalModuleList, String deployMode) {
        // 菜单降序
        menuList.sort((o1, o2) -> {
            if (o1.getSortNum().equals(o2.getSortNum())) {
                if (o1.getCreateTime().equals(o2.getCreateTime())) {
                    return 0;
                } else {
                    return o1.getCreateTime().before(o2.getCreateTime()) ? -1 : 1;
                }
            } else {
                return o2.getSortNum() - o1.getSortNum();
            }
        });

        // 如果存在服务模块，则将服务模块作为第一级，第一级的parentId=0
        boolean hasModule = Constant.DEPLOY_MODE_ALL.equals(deployMode);

        // 转换，包括服务模块、菜单、接口按钮，如果是多模块下的菜单，按服务模块做第一级
        Map<String, ResourceUserMenuDetail> menuId2NodeMap = new LinkedHashMap<>();
        // 判断是否是多服务模块的，如果是，则服务模块做第一级
        if (hasModule) {
            Set<String> moduleSet = menuList.stream().map(SysMenuEntity::getModuleType).collect(Collectors.toSet());
            totalModuleList.sort(Comparator.comparingInt(SysConfigEntity::getSortNum));
            for (SysConfigEntity moduleEntity : totalModuleList) {
                if (!moduleSet.contains(moduleEntity.getPropertyKey())) {
                    continue;
                }
                ResourceUserMenuDetail node = new ResourceUserMenuDetail();
                node.setMenuId(moduleEntity.getPropertyKey());
                node.setModule(moduleEntity.getPropertyKey());
                node.setMenuName(moduleEntity.getPropertyValue());
                node.setParentId(Constant.DEFAULT_PARENT_ID_STR);
                menuId2NodeMap.put(node.getMenuId(), node);
            }
        }

        for (SysMenuEntity entity : menuList) {
            // 独立部署需忽略工作台
            if (!hasModule && Constant.MODULE_TYPE_WORK_BENCH.equals(entity.getModuleType())) {
                continue;
            }
            ResourceUserMenuDetail node = getResourceUserMenuDetail(entity, hasModule);
            menuId2NodeMap.put(String.valueOf(entity.getMenuId()), node);
        }

        // 将子集放入父级children中
        for (Map.Entry<String, ResourceUserMenuDetail> nodeEntry : menuId2NodeMap.entrySet()) {
            ResourceUserMenuDetail node = nodeEntry.getValue();
            if (menuId2NodeMap.containsKey(node.getParentId())) {
                ResourceUserMenuDetail parentNode = menuId2NodeMap.get(node.getParentId());
                if (parentNode.getChildren() == null) {
                    parentNode.setChildren(new ArrayList<>());
                }
                parentNode.getChildren().add(node);
            }
        }

        // 只取一级节点
        return menuId2NodeMap.values().stream()
                .filter(s -> Constant.DEFAULT_PARENT_ID_STR.equals(s.getParentId()))
                .collect(Collectors.toList());
    }

    @NotNull
    private static ResourceUserMenuDetail getResourceUserMenuDetail(SysMenuEntity entity, boolean hasModule) {
        ResourceUserMenuDetail node = new ResourceUserMenuDetail();
        node.setMenuId(String.valueOf(entity.getMenuId()));
        node.setIsgroup(entity.getIsgroup());
        node.setIsmenu(entity.getIsmenu());
        node.setMenuName(entity.getMenuName());
        if (hasModule && Constant.DEFAULT_PARENT_ID.equals(entity.getParentId())) {
            // 将parentId=0的节点，parentId改为moduleType，以和module做父子关系匹配
            node.setParentId(entity.getModuleType());
        } else {
            node.setParentId(String.valueOf(entity.getParentId()));
        }
        node.setMenuPath(entity.getMenuPath());
        node.setIsoutlink(entity.getIsoutlink());
        node.setIcon(entity.getIcon());
        return node;
    }

    /**
     * 自下而上标记选中状态，如果子集选中了，则自身也标记选中
     */
    public static Boolean[] markSelect(List<MenuTreeDTO> authMenuTree, Set<String> selectIdSet, Set<String> defaultMenuIds) {
        Boolean[] hasBrotherSelectAndDisable = new Boolean[]{false, false};
        for (MenuTreeDTO dto : authMenuTree) {
            Boolean[] hasChildSelectAndDisable = new Boolean[]{false, false};
            if (CollectionUtil.isNotEmpty(dto.getChildren())) {
                hasChildSelectAndDisable = markSelect(dto.getChildren(), selectIdSet, defaultMenuIds);
            }
            if (selectIdSet.contains(dto.getMenuId()) || hasChildSelectAndDisable[0]) {
                dto.setIsselect(true);
                hasBrotherSelectAndDisable[0] = true;
            }
            if (defaultMenuIds.contains(dto.getMenuId()) || hasChildSelectAndDisable[1]) {
                dto.setDisabled(true);
                hasBrotherSelectAndDisable[1] = true;
            }
        }
        return hasBrotherSelectAndDisable;
    }
}
