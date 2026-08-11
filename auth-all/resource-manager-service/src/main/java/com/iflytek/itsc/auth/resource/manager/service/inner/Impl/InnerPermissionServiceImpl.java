package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.toolkit.JoinWrappers;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.common.utils.DozerBeanUtil;
import com.iflytek.itsc.auth.resource.manager.common.utils.MD5Util;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.common.utils.nativeCipher.SM3HMACUtil;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysDataGroupBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysMenuBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.*;
import com.iflytek.itsc.auth.resource.manager.domain.entity.*;
import com.iflytek.itsc.auth.resource.manager.domain.form.DeployMetaInfoAndMenuForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.InnerPermissionForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.PermissionElementForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.BasePageParam;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerDeployInfoService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerPermissionService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysMenuService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysUserService;
import com.iflytek.itsc.util.json.JsonUtil;
import com.iflytek.itsc.util.security.HMacUtil;
import com.iflytek.itsc.auth.common.exception.BaseBizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/12
 * @desc 同步auth的接口方法
 **/
@Service
@Slf4j
public class InnerPermissionServiceImpl implements InnerPermissionService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysMenuResourceService sysMenuResourceService;
    @Autowired
    private SysWorkSpaceService sysWorkSpaceService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysDataGroupService sysDataGroupService;
    @Autowired
    private InnerSysUserService innerSysUserService;
    @Autowired
    private SysApplicationService sysApplicationService;

    @Autowired(required = false)
    private SM3HMACUtil sm3HMACUtil;

    @Value("${security.select:default}")
    private String securityType;

    @Autowired
    UserCacheDeleteUtil userCacheDeleteUtil;

    @Autowired
    private InnerDeployInfoService innerDeployInfoService;

    @Autowired
    private InnerSysMenuService innerSysMenuService;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * 查询用户权限信息
     */
    @Override
    public ResourceUser getUserPermission(String account) {
        // 查询用户信息
        SysUserEntity userEntity = sysUserService.getByAccount(account);
        if (userEntity == null) {
            log.info("account:{} 不存在", account);
            return null;
        }
        ResourceUser user = new ResourceUser();
        user.setUserId(userEntity.getUserId());
        user.setAccount(userEntity.getAccount());
        user.setSuperAdmin(sysUserService.isSuperAdmin(userEntity.getUserId()));
        // 基础信息
        user.setBaseInfo(this.buildBaseInfo(userEntity));
        user.setRelateWorkSpaceIdSet(sysUserService.listRelatedWorkSpaceIds(userEntity.getUserId()));
        //查询用户所拥有的全部角色
        List<SysUserRoleEntity> sysUserRoleEntities = sysUserRoleService.listByUserId(userEntity.getUserId());
        if (CollectionUtils.isEmpty(sysUserRoleEntities)) {
            return user;
        }
        Map<Long, Long> workspaceToRoleMap = sysUserRoleEntities.stream().collect(Collectors.toMap(SysUserRoleEntity::getWorkSpaceId, SysUserRoleEntity::getRoleId));
        Map<Long, List<ResourceUserApi>> map = new HashMap<>();
        for (Long workspaceId : user.getRelateWorkSpaceIdSet()) {
            Long roleId = workspaceToRoleMap.get(workspaceId);
            List<SysMenuResourceEntity> sysMenuResourceEntities = sysMenuResourceService.listByRoleId(roleId);
            if (CollectionUtils.isEmpty(sysMenuResourceEntities)) {
                continue;
            }
            //将sysMenuResourceEntities 转换成List<ResourceUserApi>
            List<ResourceUserApi> resourceUserApis = sysMenuResourceEntities.stream().map(e -> {
                ResourceUserApi api = new ResourceUserApi();
                api.setMethod(e.getMethod());
                api.setPath(e.getPath());
                return api;
            }).collect(Collectors.toList());
            map.put(workspaceId, resourceUserApis);
        }
        user.setMenuResourceApiMap(map);
        return user;
    }

    @Override
    public List<ResourceUserMenuDetail> listMenus(Long userId, Long workspaceId, String deployMode) {
        // 是否超管
        boolean isSuperAdmin = sysUserService.isSuperAdmin(userId);
        // 获取空间下的菜单、按钮集
        List<SysMenuEntity> menuEntities;
        List<SysMenuResourceEntity> buttonList;
        if (isSuperAdmin) {
            // 查询全部菜单和按钮
            menuEntities = sysMenuService.listValid();
        } else {
            // 用户当前空间下的角色
            Long roleId = sysUserRoleService.getRoleIdByUserIdAndWorkspaceId(userId, workspaceId);
            // 获取当前角色对应的菜单和按钮
            menuEntities = sysMenuService.listValidByRoleId(roleId);
        }
        // 查询菜单的服务模块，做菜单树第一级
        List<SysConfigEntity> moduleList = sysConfigService.listSysConfigEntityByPropertyType(Constant.MODULE_TYPE);
        return SysMenuBuilder.buildPermissionTree(menuEntities, moduleList, deployMode);
    }

    /**
     * 查询用户空间列表
     */
    @Override
    public List<ResourceUserWorkSpace> listWorkspaces(Long userId) {
        // 是否超管
        boolean isSuperAdmin = sysUserService.isSuperAdmin(userId);
        List<SysWorkSpaceEntity> spaceEntities;
        if (isSuperAdmin) {
            spaceEntities = sysWorkSpaceService.listValid();
        } else {
            spaceEntities = sysWorkSpaceService.listValidByUserId(userId);
        }

        // 空间名称排序
        this.orderSpaceName(spaceEntities);
        // 各空间下角色数量，加上默认角色
        Map<Long, Long> spaceId2RoleCountMap = sysWorkSpaceService.listSpaceRoleCount();
        // 各空间下关联用户数量
        Map<Long, Long> spaceId2UserCountMap = sysWorkSpaceService.listSpaceUserCount();
        // 用户在各空间下的角色名称
        Map<Long, String> spaceId2RoleNameMap = sysUserRoleService.listSpaceUserRoleName(userId);

        return spaceEntities.stream()
                .map(entity -> {
                    ResourceUserWorkSpace dto = new ResourceUserWorkSpace();
                    dto.setWorkSpaceId(entity.getWorkSpaceId());
                    dto.setWorkSpaceName(entity.getWorkSpaceName());
                    dto.setRoleCount(spaceId2RoleCountMap.getOrDefault(entity.getWorkSpaceId(), 0L).intValue());
                    dto.setUserCount(spaceId2UserCountMap.getOrDefault(entity.getWorkSpaceId(), 0L).intValue());
                    if (isSuperAdmin) {
                        dto.setRoleName("超级管理员");
                    } else {
                        dto.setRoleName(spaceId2RoleNameMap.getOrDefault(entity.getWorkSpaceId(), "-"));
                    }
                    return dto;
                }).collect(Collectors.toList());
    }

    private void orderSpaceName(List<SysWorkSpaceEntity> spaceEntities) {
        // 工作空间-首字母排序，内置空间排最前
        Collator collator = Collator.getInstance(Locale.CHINA);
        spaceEntities.sort((o1, o2) -> {
            if (o1.getIsdefault() && !o2.getIsdefault()) {
                return -1;
            } else if (!o1.getIsdefault() && o2.getIsdefault()) {
                return 1;
            } else {
                return collator.compare(o1.getWorkSpaceName(), o2.getWorkSpaceName());
            }
        });
    }

    /**
     * 转换登录用户基础信息
     */
    private ResourceUserBaseInfo buildBaseInfo(SysUserEntity userEntity) {
        ResourceUserBaseInfo baseInfo = new ResourceUserBaseInfo();
        baseInfo.setAccount(userEntity.getAccount());
        baseInfo.setEmail(userEntity.getEmail());
        baseInfo.setAccount(userEntity.getAccount());
        baseInfo.setUserId(userEntity.getUserId());
        baseInfo.setPhone(userEntity.getPhone());
        baseInfo.setFeishuUserId(userEntity.getFeishuUserId());
        baseInfo.setItWorkbenchUserId(userEntity.getItWorkbenchUserId());
        baseInfo.setIdentitySource(userEntity.getIdentitySource());
        baseInfo.setLdapAccount(userEntity.getLdapAccount());
        baseInfo.setPasswd(userEntity.getPasswd());
        baseInfo.setNickName(userEntity.getNickName());
        baseInfo.setIsloggedin(userEntity.getIsloggedin());
        baseInfo.setIsactive(userEntity.getIsactive());
        baseInfo.setHeadImg(userEntity.getHeadImg());
        baseInfo.setPasswdModifyTime(userEntity.getPasswdModifyTime().getTime());
        baseInfo.setGuideState(userEntity.getGuideState());
        return baseInfo;
    }

    @Override
    @Transactional
    public void updateLogged(Long userId) {
        log.info("updateLogged,userId:{}", userId);
        if (userId == null) {
            return;
        }
        SysUserEntity userEntity = sysUserService.getById(userId);
        if (userEntity == null) {
            return;
        }
        sysUserService.lambdaUpdate()
                .set(SysUserEntity::getIsloggedin, true)
                .set(SysUserEntity::getModifyTime, userEntity.getModifyTime())
                .eq(SysUserEntity::getUserId, userId)
                .update();
    }

    @Override
    public List<SysUserEntity> listUser(List<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return new ArrayList<>();
        }
        return sysUserService.lambdaQuery()
                .in(SysUserEntity::getUserId, userIds)
                .list();
    }

    @Override
    public List<SysUserEntity> listAllUsers() {
        return sysUserService.lambdaQuery()
                .eq(SysUserEntity::getIsvalid, true)
                .list();
    }

    @Override
    public ResourceChangeFlagDTO userCountAndLatestTime() {
        ResourceChangeFlagDTO dto = JoinWrappers.lambda(SysUserEntity.class)
                .selectCount(SysUserEntity::getUserId, ResourceChangeFlagDTO::getCount)
                .selectMax(SysUserEntity::getModifyTime, ResourceChangeFlagDTO::getModifyTime)
                .one(ResourceChangeFlagDTO.class);
        if (dto != null && dto.getModifyTime() != null) {
            dto.setModifyTimeLong(dto.getModifyTime().getTime());
        }
        return dto;
    }

    @Override
    @Transactional
    public void updateLoginTime(Long userId) {
        log.info("updateLoginTime,userId:{}", userId);
        if (userId == null) {
            return;
        }
        SysUserEntity userEntity = sysUserService.getById(userId);
        if (userEntity == null) {
            return;
        }
        sysUserService.lambdaUpdate()
                .set(SysUserEntity::getLoginTime, new Date())
                .set(SysUserEntity::getModifyTime, userEntity.getModifyTime())
                .eq(SysUserEntity::getUserId, userId)
                .update();
    }

    @Override
    public void updatePassword(SysUserPageParam form) {
        log.info("updatePassword,form:{}", JsonUtil.object2Json(form));
        innerSysUserService.updatePassword(form);
    }

    /**
     * 工作台更新用户，用户名、手机、邮箱
     */
    @Override
    public void updateUser(SysUserForm form) {
        SysUserEntity entity = new SysUserEntity();
        entity.setUserId(form.getUserId());
        entity.setNickName(form.getNickName());
        entity.setAccount(form.getAccount());
        entity.setEmail(form.getEmail());
        entity.setPhone(form.getPhone());
        entity.setModifyTime(new Date());
        sysUserService.updateById(entity);

        // 查询关联用户，并推送auth变更
        SysUserEntity oldEntity = sysUserService.getById(form.getUserId());
        List<String> accounts = Collections.singletonList(oldEntity.getAccount());
        userCacheDeleteUtil.deleteUserCache(accounts);
    }

    @Override
    @Transactional
    public ResourceUser insertUser(SysUserForm form) {
        // 表单校验
        this.validateUserForm(form);
        SysUserEntity oldUser = sysUserService.getByAccount(form.getAccount());
        if (oldUser != null) {
            throw new AuthBizException("账号已存在");
        }
        // 新增用户
        SysUserEntity entity = DozerBeanUtil.convert(form, SysUserEntity.class);
        String defaultPwd = sysConfigService.getDefaultPwd();
        entity.setPasswd(HMacUtil.hMacMd5(MD5Util.getMD5(defaultPwd).toLowerCase()));
        entity.setCreateTime(new Date());
        entity.setModifyTime(new Date());
        sysUserService.save(entity);
        entity = sysUserService.getByAccount(entity.getAccount());

        // 返回用户权限
        ResourceUser user = new ResourceUser();
        user.setUserId(entity.getUserId());
        user.setAccount(entity.getAccount());
        user.setBaseInfo(this.buildBaseInfo(entity));
        return user;
    }

    private void validateUserForm(SysUserForm form) {
        if (StringUtils.isBlank(form.getAccount())) {
            throw new AuthBizException("账号不能为空");
        } else if (form.getAccount().length() > 100) {
            throw new AuthBizException("账号长度不能超过100");
        }
        if (StringUtils.isBlank(form.getNickName())) {
            throw new AuthBizException("昵称不能为空");
        } else if (form.getNickName().length() > 100) {
            throw new AuthBizException("昵称长度不能超过100");
        }
        if (StringUtils.isNotBlank(form.getEmail()) && form.getEmail().length() > 100) {
            throw new AuthBizException("邮箱长度不能超过100");
        }
        if (StringUtils.isNotBlank(form.getPhone()) && form.getPhone().length() > 20) {
            throw new AuthBizException("电话长度不能超过20");
        }
        if (StringUtils.isNotBlank(form.getFeishuUserId()) && form.getFeishuUserId().length() > 128) {
            throw new AuthBizException("飞书 user_id 长度不能超过128");
        }
        if (StringUtils.isNotBlank(form.getItWorkbenchUserId()) && form.getItWorkbenchUserId().length() > 128) {
            throw new AuthBizException("三方 IT 工作台 ID 长度不能超过128");
        }
        if (StringUtils.isNotBlank(form.getLdapAccount()) && form.getLdapAccount().length() > 128) {
            throw new AuthBizException("LDAP 账号长度不能超过128");
        }
        String identitySource = StringUtils.defaultIfBlank(form.getIdentitySource(), "LOCAL").trim().toUpperCase(Locale.ROOT);
        if (!Arrays.asList("LOCAL", "LDAP", "SYNC").contains(identitySource)) {
            throw new AuthBizException("身份来源只支持 LOCAL、LDAP、SYNC");
        }
        form.setIdentitySource(identitySource);
        if ("LDAP".equals(identitySource)) {
            form.setLdapAccount(form.getAccount());
        }
    }

    @Override
    public List<SysDataGroupEntity> findDataGroups(Long workspaceId) {
        return sysDataGroupService.listElementValidByWorkspaceId(workspaceId);

    }

    @Override
    public List<ResourceSpaceApplication> findApplications(Long workspaceId) {
        return sysApplicationService.findValidApplications(workspaceId);
    }

    /**
     * 查询业务组树-带数据单元
     */
    @Override
    public List<DataGroupTreeDTO> dataGroupTree(Long workSpaceId) {
        // 所有业务组/数据单元对象
        List<SysDataGroupEntity> allDG = sysDataGroupService.lambdaQuery()
                .eq(SysDataGroupEntity::getIsvalid, true)
                .eq(SysDataGroupEntity::getWorkSpaceId, workSpaceId)
                .orderByAsc(SysDataGroupEntity::getSortNum)
                .list();
        if (CollectionUtils.isEmpty(allDG)) return new ArrayList<>();
        List<DataGroupTreeDTO> list = SysDataGroupBuilder.buildElementTree(allDG);
        if (CollectionUtil.isNotEmpty(list)) {
            // 一级菜单父级null，页面不展示0
            list.forEach(s -> s.setParentId(null));
        }
        return list;
    }

    /**
     * 新增数据单元
     */
    @Override
    public SysDataGroupEntity insertElement(PermissionElementForm form) {
        // 合法性校验
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (workSpaceEntity == null) {
            throw new AuthBizException("工作空间不存在");
        }
        //数据单元名称空间下唯一
        Long count = sysDataGroupService.lambdaQuery()
                .eq(SysDataGroupEntity::getWorkSpaceId, form.getWorkSpaceId())
                .eq(SysDataGroupEntity::getIselement, true)
                .eq(SysDataGroupEntity::getDataGroupName, form.getDataGroupName())
                .count();
        if (count > 0) {
            throw new AuthBizException("空间下存在同名数据单元！");
        }

        // 保存业务组
        SysDataGroupEntity entity = SysDataGroupBuilder.buildPermissionElementInsertEntity(form);
        // 获取同级最后序号
        SysDataGroupEntity dataGroupEntity = sysDataGroupService.lambdaQuery()
                .eq(SysDataGroupEntity::getWorkSpaceId, entity.getWorkSpaceId())
                .eq(SysDataGroupEntity::getParentId, entity.getParentId())
                .orderByDesc(SysDataGroupEntity::getSortNum)
                .last("limit 1")
                .one();
        int sortNum = dataGroupEntity != null ? dataGroupEntity.getSortNum() : 0;
        entity.setSortNum(sortNum + 1);
        sysDataGroupService.save(entity);

        return sysDataGroupService.getById(entity.getDataGroupId());
    }

    /**
     * 更新用户新手指引状态
     */
    @Override
    public void updateGuideState(Long userId, String guideState) {
        if (userId == null) {
            return;
        }
        if (StringUtils.isBlank(guideState) || guideState.length() > 1000) {
            throw new AuthBizException("新手指引长度不能超过1000");
        }
        try {
            JsonUtil.json2Object(guideState, Map.class);
        } catch (Exception e) {
            throw new AuthBizException("新手指引状态非JSON");
        }
        SysUserEntity userEntity = sysUserService.getById(userId);
        if (userEntity == null) {
            return;
        }
        sysUserService.lambdaUpdate()
                .set(SysUserEntity::getGuideState, guideState)
                .set(SysUserEntity::getModifyTime, userEntity.getModifyTime())
                .eq(SysUserEntity::getUserId, userId)
                .update();
        // 查询关联用户，并推送auth变更
        userCacheDeleteUtil.deleteUserCache(Collections.singletonList(userEntity.getAccount()));
    }

    @Override
    public void validateIntegrality(Long userId) {
        if (Constant.DEFAULT_SECURITY_TYPE.equals(securityType)) {
            return;
        }
        SysUserEntity sysUserEntity = sysUserService.getById(userId);
        if (sysUserEntity == null) {
            throw new AuthBizException("用户信息不存在");
        }
        if (StringUtils.isNotEmpty(sysUserEntity.getSign())) {
            //获取需要加密的字段
            String originSign = sm3HMACUtil.getOriginSign(sysUserEntity);
            boolean validateIntegrality = sm3HMACUtil.verifyIntegrality(originSign, sysUserEntity.getSign().trim());
            if (!validateIntegrality) {
                throw new AuthBizException("sys_user数据完整性校验不通过");

            }
        }

        List<SysUserRoleEntity> sysUserRoleEntities = sysUserRoleService.listByUserId(userId);
        if (CollectionUtil.isNotEmpty(sysUserRoleEntities)) {
            for (SysUserRoleEntity sysUserRoleEntity : sysUserRoleEntities) {
                if (StringUtils.isNotEmpty(sysUserRoleEntity.getSign())) {
                    String originSign1 = sm3HMACUtil.getOriginSign(sysUserRoleEntity);
                    boolean validateIntegrality1 = sm3HMACUtil.verifyIntegrality(originSign1, sysUserRoleEntity.getSign().trim());
                    if (!validateIntegrality1) {
                        throw new AuthBizException("sys_user_role数据完整性校验不通过");
                    }
                }

            }
        }
    }

    @Override
    public void initCipherData() {
        if (Constant.DEFAULT_SECURITY_TYPE.equals(securityType)) {
            return;
        }
        //初始化sys_user,sys_user_role表 加密和sign值的数据
        //取出全部的数据，重新保存下即可完成加密和sign值的保存
        List<SysUserEntity> sysUserEntities = sysUserService.list();
        sysUserService.updateBatchById(sysUserEntities);
        List<SysUserRoleEntity> sysUserRoleEntities = sysUserRoleService.list();
        sysUserRoleService.updateBatchById(sysUserRoleEntities);

    }

    /**
     * 查询用户基础信息，及绑定的空间id
     */
    @Override
    public List<OtherServiceUserInfoDTO> listOtherServiceUserInfo(InnerPermissionForm form) {
        List<OtherServiceUserInfoDTO> list = JoinWrappers.lambda(SysUserEntity.class)
                .selectAs(SysUserEntity::getUserId, OtherServiceUserInfoDTO::getUserId)
                .selectAs(SysUserEntity::getAccount, OtherServiceUserInfoDTO::getAccount)
                .selectAs(SysUserEntity::getPhone, OtherServiceUserInfoDTO::getPhone)
                .selectAs(SysUserEntity::getEmail, OtherServiceUserInfoDTO::getEmail)
                .selectAs(SysUserEntity::getNickName, OtherServiceUserInfoDTO::getNickName)
                .selectAs("suws2", SysUserWorkSpaceEntity::getWorkSpaceId, OtherServiceUserInfoDTO::getWorkspaceId)
                // 连表两次，suws1用来做空间id条件筛选，suws2用来收集权限空间id
                .leftJoin(SysUserWorkSpaceEntity.class, "suws1", SysUserWorkSpaceEntity::getUserId, SysUserEntity::getUserId)
                .leftJoin(SysUserWorkSpaceEntity.class, "suws2", SysUserWorkSpaceEntity::getUserId, SysUserEntity::getUserId)
                .eq(SysUserEntity::getIsvalid, true)
                .in(CollUtil.isNotEmpty(form.getUserIds()), SysUserEntity::getUserId, form.getUserIds())
                .in(CollUtil.isNotEmpty(form.getWorkspaceIds()), "suws1", SysUserWorkSpaceEntity::getWorkSpaceId, form.getWorkspaceIds())
                .and(StrUtil.isNotBlank(form.getUserNameOrAccount()), wrapper ->
                        wrapper.like(SysUserEntity::getNickName, form.getUserNameOrAccount())
                                .or()
                                .like(SysUserEntity::getAccount, form.getUserNameOrAccount()))
                .orderByAsc(SysUserEntity::getCreateTime)
                .list(OtherServiceUserInfoDTO.class);

        // 保持顺序
        Map<Long, OtherServiceUserInfoDTO> userId2InfoMap = new LinkedHashMap<>();
        for (OtherServiceUserInfoDTO dto : list) {
            if (!userId2InfoMap.containsKey(dto.getUserId())) {
                dto.setWorkspaceIdSet(new HashSet<>());
                userId2InfoMap.put(dto.getUserId(), dto);
            }
            userId2InfoMap.get(dto.getUserId()).getWorkspaceIdSet().add(dto.getWorkspaceId());
        }

        return new ArrayList<>(userId2InfoMap.values());
    }

    @Override
    public DeployInfoDTO listDeployInfo() {
        return innerDeployInfoService.listDeployInfo();
    }

    @Override
    public void saveDeployMetaInfoAndMenu(String moduleName) {
        log.info("开始保存部署信息和菜单,moduleName:{}", moduleName);
        DeployMetaInfoAndMenuForm deployMetaInfoAndMenuForm = buildDeployMetaInfoAndMenuForm(moduleName);
        //保存部署信息
        if (deployMetaInfoAndMenuForm.getMeta() != null) {
            innerDeployInfoService.saveDeployInfo(deployMetaInfoAndMenuForm.getMeta());
        }
        if (CollectionUtil.isNotEmpty(deployMetaInfoAndMenuForm.getMenus())) {
            //保存菜单相关信息
            innerSysMenuService.saveBatch(deployMetaInfoAndMenuForm.getMenus());
        }

    }

    private DeployMetaInfoAndMenuForm buildDeployMetaInfoAndMenuForm(String moduleName) {
        try {
            String location = "classpath:menuAndMetaInfo/" + moduleName + ".json";
            Resource resource = resourceLoader.getResource(location);
            String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));
            return JSONUtil.toBean(content, DeployMetaInfoAndMenuForm.class);
        } catch (IOException e) {
            log.error("读取{}Json文件异常", moduleName, e);
            throw new BaseBizException("读取Json文件异常");
        }
    }

    @Override
    @Transactional
    public void deleteDeployMetaInfoAndMenu(String moduleName) {
        //删除部署信息
        innerDeployInfoService.deleteByModuleName(moduleName);
        //删除菜单相关联信息
        innerSysMenuService.deleteBatch(moduleName);
    }

    /**
     * 查询用户空间表总数和最后更新时间
     */
    @Override
    public ResourceChangeFlagDTO userSpaceCountAndLatestTime() {
        ResourceChangeFlagDTO dto = JoinWrappers.lambda(SysUserWorkSpaceEntity.class)
                .selectCount(SysUserWorkSpaceEntity::getId, ResourceChangeFlagDTO::getCount)
                .selectMax(SysUserWorkSpaceEntity::getCreateTime, ResourceChangeFlagDTO::getModifyTime)
                .one(ResourceChangeFlagDTO.class);
        if (dto != null && dto.getModifyTime() != null) {
            dto.setModifyTimeLong(dto.getModifyTime().getTime());
        }
        return dto;
    }

    /**
     * 分页查询用户空间表，根据空间id排序
     */
    @Override
    public Page<SysUserWorkSpaceEntity> userSpacePage(BasePageParam pageParam) {
        return JoinWrappers.lambda(SysUserWorkSpaceEntity.class)
                .orderByAsc(SysUserWorkSpaceEntity::getWorkSpaceId, SysUserWorkSpaceEntity::getId)
                .page(Page.of(pageParam.getPageNo(), pageParam.getPageSize()));
    }
}
