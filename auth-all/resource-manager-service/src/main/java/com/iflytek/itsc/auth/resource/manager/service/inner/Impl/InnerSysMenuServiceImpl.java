package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import java.util.ArrayList;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysMenuBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.MenuTreeDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysConfigEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysMenuResourceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleMenuEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysMenuForm;
import com.iflytek.itsc.auth.resource.manager.mapper.SysMenuMapper;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.iflytek.itsc.auth.resource.manager.common.constants.Constant.DM_DRIVER_NAME;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@Service
@Slf4j
public class InnerSysMenuServiceImpl implements InnerSysMenuService {
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysMenuResourceService sysMenuResourceService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysShortcutMenuService sysShortcutMenuService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Value("${spring.datasource.druid.driver-class-name}")
    private String driverClassName;

    @Autowired
    UserCacheDeleteUtil userCacheDeleteUtil;

    /**
     * 新增菜单
     */
    @Override
    @Transactional
    public void insert(SysMenuForm form) {
        // 合法校验
        form.setMenuId(null);
        if (form.getIsmenu() != null && !form.getIsmenu()) {
            form.setIsoutlink(false);
            form.setMenuPath(null);
        }
        this.validateLegal(form);

        // 保存菜单，并获取新的菜单id
        SysMenuEntity menuEntity = SysMenuBuilder.buildInsertEntity(form);
        sysMenuService.save(menuEntity);
        form.setMenuId(menuEntity.getMenuId());
        // 保存接口资源
//        saveBatchMenuResource(form);
        insertBatchMenuResource(new ArrayList<>(Collections.singletonList(form)));
        //清除缓存
        List<String> accounts = sysUserService.listAccountsByMenuId(new ArrayList<>(Collections.singletonList(form.getMenuId())));
        userCacheDeleteUtil.deleteUserCache(accounts);

    }

    /**
     * 更新菜单
     */
    @Override
    @Transactional
    public void update(SysMenuForm form) {
        // 数据有效性校验
        if (form.getIsmenu() != null && !form.getIsmenu()) {
            form.setIsoutlink(false);
            form.setMenuPath(null);
        }
        this.validateLegal(form);
        // 待更新菜单
        SysMenuEntity todoUpdateMenuEntity = SysMenuBuilder.buildUpdateEntity(form);
        // 更新菜单
        sysMenuService.updateById(todoUpdateMenuEntity);
        // 保存接口资源-全删再增
        sysMenuResourceService.deleteByMenuId(todoUpdateMenuEntity.getMenuId());
//        saveBatchMenuResource(form);
        insertBatchMenuResource(new ArrayList<>(Collections.singletonList(form)));
        //清除缓存
        List<String> accounts = sysUserService.listAccountsByMenuId(new ArrayList<>(Collections.singletonList(form.getMenuId())));
        userCacheDeleteUtil.deleteUserCache(accounts);

    }

    /**
     * 增改校验数据合法性
     */
    private void validateLegal(SysMenuForm form) {
        // 子平台类型
        Set<String> moduleKeys = sysConfigService.listModuleKeys(Constant.MODULE_TYPE);
        if (!moduleKeys.contains(form.getModuleType())) {
            throw new AuthBizException("子平台不存在");
        }
        if (form.getMenuId() != null) {
            SysMenuEntity oldEntity = sysMenuService.getById(form.getMenuId());
            if (oldEntity == null) {
                throw new AuthBizException("菜单不存在");
            }
            if (form.getParentId().equals(form.getMenuId())) {
                throw new AuthBizException("不能以自身作为父级");
            }
        }
        if (!Constant.DEFAULT_PARENT_ID.equals(form.getParentId())) {
            SysMenuEntity parentEntity = sysMenuService.getById(form.getParentId());
            if (parentEntity == null) {
                throw new AuthBizException("父级不存在");
            }
            if (!parentEntity.getModuleType().equals(form.getModuleType())) {
                throw new AuthBizException("父级服务模块不匹配");
            }
        }
    }

    /**
     * 删除菜单
     */
    @Override
    @Transactional
    public void delete(SysMenuForm form) {
        Long menuId = form.getMenuId();
        // 查询所有菜单，判断删除菜单是否存在，并递归获取所有子集id
        List<SysMenuEntity> allMenuList = sysMenuService.listValid();
        if (allMenuList.stream().noneMatch(s -> s.getMenuId().equals(menuId))) {
            throw new AuthBizException("菜单不存在");
        }

        Map<Long, Set<Long>> parentId2ChildIdsMap = allMenuList.stream()
                .collect(Collectors.groupingBy(SysMenuEntity::getParentId,
                        Collectors.mapping(SysMenuEntity::getMenuId, Collectors.toSet())));

        List<Long> menuIds = new ArrayList<>();
        this.getAllChildMenuIds(menuIds, parentId2ChildIdsMap, menuId);
        // 待删除加上自身
        menuIds.add(menuId);
        for (SysMenuEntity entity : allMenuList) {
            if (menuId.equals(entity.getMenuId()) && entity.getIsdefault()) {
                throw new AuthBizException("默认菜单不可删除");
            }
            if (menuIds.contains(entity.getMenuId()) && entity.getIsdefault()) {
                throw new AuthBizException("子集存在默认菜单[" + entity.getMenuName() + "]，不可删除");
            }
        }
        // 删除角色和菜单关系-自身及子集
        sysRoleMenuService.deleteByMenuIds(menuIds);
        // 删除菜单资源
        sysMenuResourceService.deleteByMenuIds(menuIds);
        // 删除菜单-自身及子集
        sysMenuService.deleteByMenuIds(menuIds);
        //删除快捷菜单数据
        sysShortcutMenuService.deleteByMenuIds(menuIds);
        //清除缓存
        List<String> accounts = sysUserService.listAccountsByMenuId(new ArrayList<>(Collections.singletonList(form.getMenuId())));
        userCacheDeleteUtil.deleteUserCache(accounts);
    }

    /**
     * 从所有菜单id中获取子集
     */
    private void getAllChildMenuIds(List<Long> childMenuIds, Map<Long, Set<Long>> parentId2MenuIdsMap, Long menuId) {
        if (parentId2MenuIdsMap.containsKey(menuId)) {
            childMenuIds.addAll(parentId2MenuIdsMap.get(menuId));
            for (Long childId : parentId2MenuIdsMap.get(menuId)) {
                this.getAllChildMenuIds(childMenuIds, parentId2MenuIdsMap, childId);
            }
        }
    }

    /**
     * 查询菜单详情
     */
    @Override
    public SysMenuForm getDetail(Long menuId) {
        if (menuId == null) throw new AuthBizException("id不能为空");
        SysMenuEntity menuEntity = sysMenuService.getById(menuId);
        if (menuEntity == null) {
            throw new AuthBizException("记录不存在");
        }
        List<SysMenuResourceEntity> menuResourceEntityList = sysMenuResourceService.listByMenuId(menuId);
        return SysMenuBuilder.buildMenuDetail(menuEntity, menuResourceEntityList);
    }

    /**
     * 查询全部菜单树
     */
    @Override
    public List<MenuTreeDTO> getModuleSubTree(String module) {
        List<SysMenuEntity> menuList = sysMenuService.listByModule(module);
        if (CollectionUtil.isEmpty(menuList)) return new ArrayList<>();

        return SysMenuBuilder.buildMenuResourceTree(false, menuList, null);
    }

    @Override
    @Transactional
    public void saveBatch(List<SysMenuForm> sysMenuForms) {
        //批量保存菜单和菜单角色关系表
        saveBatchMenu(sysMenuForms);
        //批量保存菜单资源关系表
        saveBatchMenuResource(sysMenuForms);
        //清除缓存
        List<Long> menuIds = sysMenuForms.stream().map(SysMenuForm::getMenuId).collect(Collectors.toList());
        List<String> accounts = sysUserService.listAccountsByMenuId(menuIds);
        userCacheDeleteUtil.deleteUserCache(accounts);
    }

    private void saveBatchMenu(List<SysMenuForm> sysMenuForms) {
        //处理原始数据
        List<SysMenuForm> menuForms = processSysMenuForms(sysMenuForms);
        if (CollectionUtil.isEmpty(menuForms)) {
            return;
        }
        //批量保存菜单数据
        List<SysMenuEntity> sysMenuEntities = menuForms.stream().map(SysMenuBuilder::buildSaveBatchEntity).collect(Collectors.toList());
        //dm数据库需要SET IDENTITY_INSERT为ON,才能对自增的列进行手动赋值
        if (DM_DRIVER_NAME.equals(driverClassName)) sysMenuMapper.startIdentityInsert();
        sysMenuService.saveBatch(sysMenuEntities);
        if (DM_DRIVER_NAME.equals(driverClassName)) sysMenuMapper.endIdentityInsert();
        //保存角色菜单关系
        List<SysRoleMenuEntity> sysRoleMenuEntities = new ArrayList<>();
        for (SysMenuEntity sysMenuEntity : sysMenuEntities) {
            SysRoleMenuEntity sysRoleMenuEntity = new SysRoleMenuEntity();
            //默认的空间管理员角色roleId是2
            sysRoleMenuEntity.setRoleId(2L);
            sysRoleMenuEntity.setMenuId(sysMenuEntity.getMenuId());
            sysRoleMenuEntities.add(sysRoleMenuEntity);
        }
        sysRoleMenuService.saveBatch(sysRoleMenuEntities);
    }

    private void saveBatchMenuResource(List<SysMenuForm> sysMenuForms) {
        List<String> menuNos = sysMenuForms.stream()
                .map(SysMenuForm::getMenuNo)
                .collect(Collectors.toList());
        // 批量查询现有的菜单信息
        List<SysMenuEntity> existSysMenuEntities = sysMenuService.findByMenuNo(menuNos);
        Map<String, SysMenuEntity> existingMenuMap = existSysMenuEntities.stream()
                .collect(Collectors.toMap(SysMenuEntity::getMenuNo, Function.identity()));
        List<Long> menuIds = new ArrayList<>();
        // 遍历输入的菜单表单列表
        for (SysMenuForm inputMenuForm : sysMenuForms) {
            SysMenuEntity existingMenu = existingMenuMap.get(inputMenuForm.getMenuNo());
            if (existingMenu != null) {
                // 如果数据库中已经存在该条菜单，则更新其ID
                inputMenuForm.setMenuId(existingMenu.getMenuId());
            }
            menuIds.add(inputMenuForm.getMenuId());
        }

        // 删除旧的快捷菜单
        sysMenuResourceService.deleteByMenuIds(menuIds);
        // 保存新的接口资源
        insertBatchMenuResource(sysMenuForms);
    }

    @Override
    @Transactional
    public void deleteBatch(String moduleName) {
        //构建需要删除的menuId 列表
        Set<Long> needDeleteMenuIds = buildNeedDeleteMenuIds(moduleName);
        if (CollectionUtil.isEmpty(needDeleteMenuIds)) {
            return;
        }
        //删除菜单数据
        sysMenuService.deleteByMenuIds(needDeleteMenuIds);
        //删除角色菜单关系
        sysRoleMenuService.deleteByMenuIds(needDeleteMenuIds);
        //删除菜单资源
        sysMenuResourceService.deleteByMenuIds(needDeleteMenuIds);
        //清除缓存
        List<String> accounts = sysUserService.listAccountsByMenuId(needDeleteMenuIds);
        userCacheDeleteUtil.deleteUserCache(accounts);
    }

    /**
     * 构建需要删除的菜单ID列表。
     *
     * @param moduleName 模块名称，用于查找属于这个moduleName的id规则。
     * @return 返回一个包含所有需要删除的菜单ID的列表。
     */
    private Set<Long> buildNeedDeleteMenuIds(String moduleName) {
        //查找属于这个moduleName的id 规则
        SysConfigEntity sysConfigEntity = sysConfigService.listSysConfigEntityByPropertyTypeAndKey(Constant.DEPLOY_MODULE, moduleName);
        long startId = Long.parseLong(sysConfigEntity.getRemark());
        List<Long> needDeleteMenuIds = new ArrayList<>();
        //查找出所有默认菜单数据
        List<SysMenuEntity> allMenuList = sysMenuService.listValid();
        for (SysMenuEntity sysMenuEntity : allMenuList) {
            //规定每个模块的menuId 区间为[startId,startId+100)
            if (startId <= sysMenuEntity.getMenuId() && sysMenuEntity.getMenuId() < startId + 100) {
                needDeleteMenuIds.add(sysMenuEntity.getMenuId());
                //级联删除当前菜单父菜单：查找当前需要删除的菜单的父菜单，子菜单是否都需删除，如果是，删除父菜单
                buildSingleChildParents(needDeleteMenuIds, allMenuList, sysMenuEntity, startId);
            }
        }
        //去重（去除父菜单正好在本次删除模块里的菜单）
        Set<Long> needDeleteMenuIds1 = new HashSet<>(needDeleteMenuIds);
        //查找当前需要被删除的菜单的子菜单，如果子菜单不是全部需要删除，则不删除当前的菜单
        Iterator<Long> iterator = needDeleteMenuIds1.iterator();
        while (iterator.hasNext()) {
            Long menuId = iterator.next();
            boolean needDeleteFlag = isNeedDelete(allMenuList, menuId, needDeleteMenuIds1);
            if (!needDeleteFlag) {
                iterator.remove();
            }
        }
        return needDeleteMenuIds1;
    }

    /**
     * 判断菜单是否需要删除。
     *
     * @param allMenuList        所有菜单列表。
     * @param menuId             需要判断的菜单ID。
     * @param needDeleteMenuIds1 需要删除的菜单ID集合。
     * @return 如果该菜单的所有子菜单都在需要删除的菜单ID集合中，返回true；否则返回false。
     */
    private boolean isNeedDelete(List<SysMenuEntity> allMenuList, Long menuId, Set<Long> needDeleteMenuIds1) {
        //查找当前需要删除的菜单的所有子菜单
        List<Long> childMenusIds = allMenuList.stream().filter(x -> Objects.equals(x.getParentId(), menuId)).map(SysMenuEntity::getMenuId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(childMenusIds)) {
            return true;
        }
        for (Long childMenuId : childMenusIds) {
            //如果有子菜单 不在本次删除的列表内，则当前菜单不被删除
            if (!needDeleteMenuIds1.contains(childMenuId)) {
                return false;
            }
        }
        //如果所有的子菜单都在当前需要删除的列表里，再看子菜单的子菜单
        for (Long childMenuId : childMenusIds) {
            return isNeedDelete(allMenuList, childMenuId, needDeleteMenuIds1);
        }

        return true;
    }

    /**
     * 级联查找并删除所有只有唯一子菜单的父菜单。（唯一子菜单是需要被删除的菜单）
     *
     * @param needDeleteMenuIds 需要被删除的菜单ID列表。
     * @param allMenuList       所有的菜单列表。
     * @param sysMenuEntity     当前要被删除的菜单实体。
     * @return 返回更新后的需要被删除的菜单ID列表
     */
    private List<Long> buildSingleChildParents
    (List<Long> needDeleteMenuIds, List<SysMenuEntity> allMenuList, SysMenuEntity sysMenuEntity, Long startId) {
        //查找当前要被删除的菜单的父菜单
        SysMenuEntity parentSysMenuEntity = allMenuList.stream().filter(x -> Objects.equals(x.getMenuId(), sysMenuEntity.getParentId())).findFirst().orElse(null);
        if (parentSysMenuEntity != null) {
            //查找这条父菜单数据的所有子菜单
            List<SysMenuEntity> childMenus = allMenuList.stream().filter(x -> Objects.equals(x.getParentId(), parentSysMenuEntity.getMenuId())).collect(Collectors.toList());
            //当前所有需要删除的子菜单
            List<SysMenuEntity> filterChildMenus = childMenus.stream().filter(x -> startId <= x.getMenuId() && x.getMenuId() < startId + 100).collect(Collectors.toList());
            //所有的子菜单都在当前需要删除的菜单列表里
            if (filterChildMenus.size() == childMenus.size()) {
                needDeleteMenuIds.add(parentSysMenuEntity.getMenuId());
                return buildSingleChildParents(needDeleteMenuIds, allMenuList, parentSysMenuEntity, startId);
            } else {
                return needDeleteMenuIds;
            }
        } else {
            return needDeleteMenuIds;
        }
    }

    /**
     * 对传入的菜单列表进行处理，如果数据库中已经存在相同的menuNo，则不保存该条数据，并修改对应数据的parent_id为数据库已经存在的menu_id。
     *
     * @param sysMenuForms 需要处理的菜单列表。
     * @return 返回处理后的菜单列表，如果数据库中已经存在相同的menuNo，则不包含这些数据；否则，返回原列表。
     */
    private List<SysMenuForm> processSysMenuForms(List<SysMenuForm> sysMenuForms) {
        List<String> menuNos = sysMenuForms.stream().map(SysMenuForm::getMenuNo).collect(Collectors.toList());
        List<SysMenuEntity> existSysMenuEntitys = sysMenuService.findByMenuNo(menuNos);
        if (CollectionUtil.isEmpty(existSysMenuEntitys)) {
            return sysMenuForms;
        }
        //过滤掉数据库已经存在的menuNo的数据
        List<String> existMenuNo = existSysMenuEntitys.stream().map(SysMenuEntity::getMenuNo).collect(Collectors.toList());
        List<SysMenuForm> filterSysMenuForms = sysMenuForms.stream().filter(sysMenuForm -> !existMenuNo.contains(sysMenuForm.getMenuNo())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sysMenuForms)) {
            return filterSysMenuForms;
        }
        //构建一个map，key是数据库已经存在的的menuNo，value是数据库对应的menuId
        Map<String, Long> existMenuNo2MenuId = existSysMenuEntitys.stream().collect(Collectors.toMap(SysMenuEntity::getMenuNo, SysMenuEntity::getMenuId));
        //构建一个map key是本次接口传的的menuId,value是数据库存在的相同menuNo的menuId
        Map<Long, Long> existMenuId2IdMap = sysMenuForms.stream()
                .filter(sysMenuForm -> existMenuNo2MenuId.containsKey(sysMenuForm.getMenuNo()))
                .collect(Collectors.toMap(SysMenuForm::getMenuId, sysMenuForm -> existMenuNo2MenuId.get(sysMenuForm.getMenuNo())));
        //将需要保存的数据的parentId替换成数据库已经存在parentId
        for (SysMenuForm sysMenuForm1 : filterSysMenuForms) {
            if (existMenuId2IdMap.containsKey(sysMenuForm1.getParentId())) {
                sysMenuForm1.setParentId(existMenuId2IdMap.get(sysMenuForm1.getParentId()));
            }
        }

        // 对需要新增的文件重新赋值菜单id，因为可能和用户自定义的外链等菜单id冲突
        this.resetMenuId(filterSysMenuForms);

        return filterSysMenuForms;
    }

    /**
     * 重新赋值菜单id
     */
    private void resetMenuId(List<SysMenuForm> filterSysMenuForms) {
        if (CollUtil.isEmpty(filterSysMenuForms)) {
            return;
        }

        // 查询当前库里最大的menuId
        Long maxMenuId = sysMenuService.getMaxMenuId();

        // 赋值新id，并整理新旧菜单IdMap
        Map<Long, Long> oldId2NewId = new HashMap<>();
        for (SysMenuForm form : filterSysMenuForms) {
            maxMenuId += 1;
            if (form.getMenuId() != null) {
                oldId2NewId.put(form.getMenuId(), maxMenuId);
            }
            form.setMenuId(maxMenuId);
        }

        // 赋值父级id
        for (SysMenuForm form : filterSysMenuForms) {
            if (form.getParentId() != null && oldId2NewId.containsKey(form.getParentId())) {
                form.setParentId(oldId2NewId.get(form.getParentId()));
            }
        }
    }

    private void insertBatchMenuResource(List<SysMenuForm> forms) {
        List<SysMenuResourceEntity> menuResourceEntities = new ArrayList<>();
        forms.forEach(form -> {
            if (CollectionUtil.isNotEmpty(form.getApiList())) {
                for (int i = 0; i < form.getApiList().size(); i++) {
                    SysMenuForm.ApiForm apiForm = form.getApiList().get(i);
                    SysMenuResourceEntity menuResourceEntity = new SysMenuResourceEntity();
                    menuResourceEntity.setMenuId(form.getMenuId());
                    menuResourceEntity.setPath(apiForm.getPath());
                    menuResourceEntity.setMethod(apiForm.getMethod());
                    menuResourceEntities.add(menuResourceEntity);
                }
            }
        });
        // 保存按钮、接口
        if (CollectionUtil.isNotEmpty(menuResourceEntities)) {
            sysMenuResourceService.saveBatch(menuResourceEntities);
        }
    }

}
