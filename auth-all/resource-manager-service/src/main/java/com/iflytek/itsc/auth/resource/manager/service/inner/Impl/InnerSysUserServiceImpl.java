package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.common.utils.DozerBeanUtil;
import com.iflytek.itsc.auth.resource.manager.common.utils.MD5Util;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysUserBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysWorkSpaceUserBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.UserDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.*;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserDeleteForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysUserPageParam;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysUserService;
import com.iflytek.itsc.util.security.HMacUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/11
 * @desc
 **/
@Service
@Slf4j
public class InnerSysUserServiceImpl implements InnerSysUserService {
    private static final Set<String> SUPPORTED_IDENTITY_SOURCES =
            new HashSet<>(Arrays.asList("LOCAL", "LDAP", "SYNC"));

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysUserWorkSpaceService sysUserWorkSpaceService;
    @Autowired
    private SysWorkSpaceService sysWorkSpaceService;
    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    UserCacheDeleteUtil userCacheDeleteUtil;

    /**
     * 新增用户
     * @param checkSpaceAuth 是否需要校验form中的空间在不在权限内
     */
    @Override
    @Transactional
    public void insertUser(SysUserForm form, boolean checkSpaceAuth) {
        // 合法性校验
        this.validateLegal(form, checkSpaceAuth);

        // 保存用户
        SysUserEntity userEntity = SysUserBuilder.buildInsertEntity(form);
        // 默认密码
        String defaultPwd = sysConfigService.getDefaultPwd();
        userEntity.setPasswd(HMacUtil.hMacMd5(MD5Util.getMD5(defaultPwd).toLowerCase()));
        userEntity.setPasswdModifyTime(new Date());
        sysUserService.save(userEntity);
        userEntity = sysUserService.getByAccount(form.getAccount());
        // 保存工作空间关系
        if (CollectionUtil.isNotEmpty(form.getWorkSpaceIdList())) {
            SysUserEntity finalUserEntity = userEntity;
            List<SysUserWorkSpaceEntity> userWorkSpaceEntityList = form.getWorkSpaceIdList().stream()
                    .map(s -> SysWorkSpaceUserBuilder.buildUserWorkSpaceEntity(finalUserEntity.getUserId(), s))
                    .collect(Collectors.toList());
            sysUserWorkSpaceService.saveBatch(userWorkSpaceEntityList);
        }
    }

    /**
     * 更新用户，包括工作台处更新
     */
    @Override
    @Transactional
    public void updateUser(SysUserForm form) {
        // 合法性校验
        this.validateLegal(form, false);

        // 查询关联用户，并推送auth变更
        SysUserEntity oldEntity = sysUserService.getById(form.getUserId());

        // 查询待删除的空间，待新增的空间关系
        Set<Long> oldSpaceIdSet = sysUserWorkSpaceService.listSpaceIdByUserId(form.getUserId());
        List<Long> formSpaceIds = form.getWorkSpaceIdList() == null ? new ArrayList<>() : form.getWorkSpaceIdList();
        List<Long> deleteSpaceIds = oldSpaceIdSet.stream()
                .filter(s -> !formSpaceIds.contains(s))
                .collect(Collectors.toList());
        List<Long> insertSpaceIds = formSpaceIds.stream()
                .filter(s -> !oldSpaceIdSet.contains(s))
                .collect(Collectors.toList());

        // 更新用户信息
        SysUserEntity entity = SysUserBuilder.buildUpdateEntity(form);
        sysUserService.updateById(entity);

        // 删除空间关系，角色关系
        if (!CollectionUtils.isEmpty(deleteSpaceIds)) {
            sysUserRoleService.deleteByUserIdSpaceIds(form.getUserId(), deleteSpaceIds);
            sysUserWorkSpaceService.deleteByUserIdSpaceIds(form.getUserId(), deleteSpaceIds);
        }

        // 新增用户空间关系
        if (!CollectionUtils.isEmpty(insertSpaceIds)) {
            List<SysUserWorkSpaceEntity> collect = insertSpaceIds.stream()
                    .map(s -> SysWorkSpaceUserBuilder.buildUserWorkSpaceEntity(form.getUserId(), s))
                    .collect(Collectors.toList());
            sysUserWorkSpaceService.saveBatch(collect);
        }

        // 查询关联用户，并推送auth变更
        if (oldEntity != null && StringUtils.isNotBlank(oldEntity.getAccount())) {
            List<String> accounts = Collections.singletonList(oldEntity.getAccount());
            userCacheDeleteUtil.deleteUserCache(accounts);
        }
    }

    /**
     * 增改表单校验
     * 包括用户池新增、更新，用户管理新增，其中用户管理新增时，只能选择一个空间，且需在用户权限范围内
     */
    private void validateLegal(SysUserForm form, boolean checkSpaceAuth) {
        normalizeAndValidateIdentity(form);

        // 登录账号不能重复
        Long count = sysUserService.lambdaQuery()
                .eq(SysUserEntity::getAccount, form.getAccount())
                .ne(null != form.getUserId(), SysUserEntity::getUserId, form.getUserId())
                .count();
        if (count > 0) {
            throw new AuthBizException("登录账号重复！");
        }

        // 空间是否存在
        if (CollectionUtil.isNotEmpty(form.getWorkSpaceIdList())) {
            List<SysWorkSpaceEntity> workSpaceEntities = sysWorkSpaceService.listByIds(form.getWorkSpaceIdList());
            if (workSpaceEntities.size() != form.getWorkSpaceIdList().size()) {
                throw new AuthBizException("工作空间不存在");
            }
        }

        // 空间/用户管理，新增，需要判断空间权限
        if (checkSpaceAuth) {
            if (CollectionUtil.isEmpty(form.getWorkSpaceIdList()) || form.getWorkSpaceIdList().size() != 1) {
                throw new AuthBizException("工作空间必选且唯一");
            }
            if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceIdList().get(0))) {
                throw new AuthBizException("无新增用户所属工作空间权限");
            }
        }
    }

    private void normalizeAndValidateIdentity(SysUserForm form) {
        String identitySource = StringUtils.defaultIfBlank(form.getIdentitySource(), "LOCAL")
                .trim()
                .toUpperCase(Locale.ROOT);
        if (!SUPPORTED_IDENTITY_SOURCES.contains(identitySource)) {
            throw new AuthBizException("身份来源只支持 LOCAL、LDAP、SYNC");
        }
        form.setIdentitySource(identitySource);
        if (StringUtils.isNotBlank(form.getFeishuUserId())) {
            form.setFeishuUserId(form.getFeishuUserId().trim());
        }
        if (StringUtils.isNotBlank(form.getItWorkbenchUserId())) {
            form.setItWorkbenchUserId(form.getItWorkbenchUserId().trim());
        }
        if ("LDAP".equals(identitySource)) {
            form.setLdapAccount(StringUtils.defaultIfBlank(form.getAccount(), form.getLdapAccount()));
        } else if (StringUtils.isNotBlank(form.getLdapAccount())) {
            form.setLdapAccount(form.getLdapAccount().trim());
        } else {
            form.setLdapAccount(null);
        }
    }

    /**
     * 删除用户
     */
    @Override
    @Transactional
    public void deleteUser(SysUserDeleteForm form) {
       if(sysUserService.isSuperAdmin(form.getUserId())) {
           throw new AuthBizException("超级管理员禁止被删除");
       }
        // 被空间引用的用户不能被删除
        Long count = sysUserWorkSpaceService.countByUser(form.getUserId());
        if (count > 0) {
            throw new AuthBizException("该用户关联了空间，请先取消关联！");
        }
        // 查询关联用户，并推送auth变更
        SysUserEntity userEntity = sysUserService.getById(form.getUserId());

        List<Long> userIds = Collections.singletonList(form.getUserId());
        // 删除用户角色关系
        sysUserRoleService.deleteByUserIds(userIds);
        // 删除空间关系
        sysUserWorkSpaceService.deleteByUserIds(userIds);
        // 删除用户
        sysUserService.removeByIds(userIds);

        // 查询关联用户，并推送auth变更
        if (userEntity != null && StringUtils.isNotBlank(userEntity.getAccount())) {
            List<String> accounts = Collections.singletonList(userEntity.getAccount());
            userCacheDeleteUtil.deleteUserCache(accounts);
        }
    }

    /**
     * 变更启用状态
     */
    @Override
    @Transactional
    public void changeActive(SysUserForm form) {
        if(sysUserService.isSuperAdmin(form.getUserId())) {
            throw new AuthBizException("超级管理员禁止改变状态");
        }
        if (null == form.getUserId()) throw new AuthBizException("用户id不能为空");
        if (form.getIsactive() == null) throw new AuthBizException("启用状态不能为空");
        sysUserService.lambdaUpdate()
                .set(SysUserEntity::getIsactive, form.getIsactive())
                .set(SysUserEntity::getModifyTime, new Date())
                .eq(SysUserEntity::getUserId, form.getUserId())
                .update();

        // 查询关联用户，并推送auth变更
        SysUserEntity userEntity = sysUserService.getById(form.getUserId());
        if (userEntity != null && StringUtils.isNotBlank(userEntity.getAccount())) {
            List<String> accounts = Collections.singletonList(userEntity.getAccount());
            userCacheDeleteUtil.deleteUserCache(accounts);
        }
    }

    private void checkPassword(String userId, String oldPwd) {
        if (StringUtils.isBlank(userId)) throw new AuthBizException("用户id不能为空");
        // 生成加密密码
        String password = HMacUtil.hMacMd5(oldPwd);
        SysUserEntity user = sysUserService.lambdaQuery()
                .eq(SysUserEntity::getUserId, userId)
                .eq(SysUserEntity::getPasswd, password)
                .one();
        if (user == null) {
            throw new AuthBizException("旧密码不正确！");
        }
    }

    /**
     * 重置密码
     */
    @Override
    @Transactional
    public void resetPassword(SysUserPageParam form) {
        if (form.getUserId() == null) throw new AuthBizException("用户id不能为空");
        // 获取初始化明文密码
        String defaultPwd = sysConfigService.getDefaultPwd();

        sysUserService.lambdaUpdate()
                .set(SysUserEntity::getPasswd, HMacUtil.hMacMd5(MD5Util.getMD5(defaultPwd).toLowerCase()))
                .set(SysUserEntity::getModifyTime, new Date())
                .set(SysUserEntity::getPasswdModifyTime, new Date())
                .eq(SysUserEntity::getUserId, form.getUserId())
                .update();

        // 查询关联用户，并推送auth变更
        SysUserEntity userEntity = sysUserService.getById(form.getUserId());
        if (userEntity != null && StringUtils.isNotBlank(userEntity.getAccount())) {
            List<String> accounts = Collections.singletonList(userEntity.getAccount());
            userCacheDeleteUtil.deleteUserCache(accounts);
        }
    }

    /**
     * 分页查询
     */
    @Override
    public Page<UserDTO> page(SysUserPageParam param) {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserEntity::getIsvalid, true);
        wrapper.in(CollUtil.isNotEmpty(param.getUserIds()), SysUserEntity::getUserId, param.getUserIds());
        if (StringUtils.isNotBlank(param.getNickName())) {
            wrapper.and(s -> s.like(SysUserEntity::getNickName, param.getNickName()).or().like(SysUserEntity::getAccount, param.getNickName()));
        }
        wrapper.orderByAsc(SysUserEntity::getCreateTime);
        Page<SysUserEntity> queryPage = new Page<>(param.getPageNo(), param.getPageSize());
        queryPage = sysUserService.page(queryPage, wrapper);

        Page<UserDTO> page = DozerBeanUtil.convert(queryPage, UserDTO.class);
        if (CollectionUtil.isEmpty(page.getRecords())) {
            return page;
        }

        // 补充所属空间（角色）、是否超管属性
        this.fillSpaceRole(page);
        return page;
    }

    /**
     * 补充所属空间（角色）、是否超管属性
     */
    private void fillSpaceRole(Page<UserDTO> page) {
        List<Long> userIds = page.getRecords().stream().map(UserDTO::getUserId).collect(Collectors.toList());

        // 查询用户空间关系
        List<SysUserWorkSpaceEntity> userWorkSpaceEntities = sysUserWorkSpaceService.listByUserIds(userIds);
        // 查询用户角色关系
        List<SysUserRoleEntity> userRoleEntities = sysUserRoleService.listByUserIds(userIds);

        // 查询空间id->空间名称
        Set<Long> spaceIds = userWorkSpaceEntities.stream().map(SysUserWorkSpaceEntity::getWorkSpaceId).collect(Collectors.toSet());
        List<SysWorkSpaceEntity> workSpaceEntities = spaceIds.isEmpty()
                ? new ArrayList<>()
                : sysWorkSpaceService.listByIds(spaceIds);
        Map<Long, String> spaceId2NameMap = workSpaceEntities.stream()
                .collect(Collectors.toMap(SysWorkSpaceEntity::getWorkSpaceId, SysWorkSpaceEntity::getWorkSpaceName));

        // 查询角色id->角色名称，超管角色ids
        Set<Long> roleIds = userRoleEntities.stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toSet());
        List<SysRoleEntity> roleEntities = roleIds.isEmpty()
                ? new ArrayList<>()
                : sysRoleService.listByIds(roleIds);
        Set<Long> superadminRoleSet = new HashSet<>();
        Map<Long, String> roleId2NameMap = new HashMap<>();
        for (SysRoleEntity roleEntity : roleEntities) {
            roleId2NameMap.put(roleEntity.getRoleId(), roleEntity.getRoleName());
            if (roleEntity.getIssuperadmin()) {
                superadminRoleSet.add(roleEntity.getRoleId());
            }
        }

        // 从角色关系，收集空间(角色)，超管用户ids
        Map<Long, Map<Long, Set<Long>>> userId2SpaceId2RoleIdMap = userRoleEntities.stream()
                .collect(Collectors.groupingBy(SysUserRoleEntity::getUserId,
                        Collectors.groupingBy(SysUserRoleEntity::getWorkSpaceId,
                                Collectors.mapping(SysUserRoleEntity::getRoleId, Collectors.toSet()))));
        Map<Long, Map<Long, String>> userId2SpaceId2RoleNameMap = new HashMap<>();
        Set<Long> superadminUserIdSet = new HashSet<>();
        for (Map.Entry<Long, Map<Long, Set<Long>>> userEntry : userId2SpaceId2RoleIdMap.entrySet()) {
            Map<Long, String> spaceId2RoleNameMap = new HashMap<>();
            for (Map.Entry<Long, Set<Long>> spaceEntry : userEntry.getValue().entrySet()) {
                String roleNames = spaceEntry.getValue().stream()
                        .map(roleId -> roleId2NameMap.getOrDefault(roleId, "-"))
                        .collect(Collectors.joining(","));
                spaceId2RoleNameMap.put(spaceEntry.getKey(), roleNames);
                // 用户是否超管
                if (spaceEntry.getValue().stream().anyMatch(superadminRoleSet::contains)) {
                    superadminUserIdSet.add(userEntry.getKey());
                }
            }
            userId2SpaceId2RoleNameMap.put(userEntry.getKey(), spaceId2RoleNameMap);
        }

        // 补充未绑定角色的空间(角色),角色显示“-”
        Map<Long, List<Long>> userId2SpaceIdMap = userWorkSpaceEntities.stream()
                .collect(Collectors.groupingBy(SysUserWorkSpaceEntity::getUserId,
                        Collectors.mapping(SysUserWorkSpaceEntity::getWorkSpaceId, Collectors.toList())));
        for (Map.Entry<Long, List<Long>> userEntry : userId2SpaceIdMap.entrySet()) {
            Map<Long, Set<Long>> spaceEntry = userId2SpaceId2RoleIdMap.get(userEntry.getKey());
            for (Long spaceId : userEntry.getValue()) {
                // 已绑定角色的空间忽略
                if (spaceEntry != null && spaceEntry.containsKey(spaceId)) {
                    continue;
                }
                if (!userId2SpaceId2RoleNameMap.containsKey(userEntry.getKey())) {
                    userId2SpaceId2RoleNameMap.put(userEntry.getKey(), new HashMap<>());
                }
                userId2SpaceId2RoleNameMap.get(userEntry.getKey()).put(spaceId, "-");
            }
        }

        // 补充到结果集
        for (UserDTO dto : page.getRecords()) {
            if (superadminUserIdSet.contains(dto.getUserId())) {
                dto.setIssuperadmin(1);
            } else {
                dto.setIssuperadmin(0);
            }
            dto.setSpaceRoleMap(userId2SpaceId2RoleNameMap.getOrDefault(dto.getUserId(), new HashMap<>()));
            if (!dto.getSpaceRoleMap().isEmpty()) {
                List<String> list = dto.getSpaceRoleMap().entrySet().stream()
                        .map(s -> spaceId2NameMap.getOrDefault(s.getKey(), "-") + "(" + s.getValue() + ")")
                        .collect(Collectors.toList());
                dto.setSpaceRole(list);
            }
            dto.setWorkSpaceIdList(userId2SpaceIdMap.getOrDefault(dto.getUserId(), new ArrayList<>()));
        }
    }

    @Override
    @Transactional
    public void updatePassword(SysUserPageParam form) {
        if (form.getUserId() == null) throw new AuthBizException("用户id不能为空");
        if (StringUtils.isEmpty(form.getOldPwd())) throw new AuthBizException("旧密码不能为空");
        if (StringUtils.isEmpty(form.getPasswd())) throw new AuthBizException("新密码不能为空");
        checkPassword(form.getUserId(),form.getOldPwd());
        // 生成加密密码
        String password = HMacUtil.hMacMd5(form.getPasswd());
        sysUserService.lambdaUpdate()
                .set(SysUserEntity::getPasswd, password)
                .set(SysUserEntity::getPasswdModifyTime, new Date())
                .set(SysUserEntity::getModifyTime, new Date())
                .eq(SysUserEntity::getUserId, form.getUserId())
                .update();

        // 查询关联用户，并推送auth变更
        SysUserEntity userEntity = sysUserService.getById(form.getUserId());
        if (userEntity != null && StringUtils.isNotBlank(userEntity.getAccount())) {
            List<String> accounts = Collections.singletonList(userEntity.getAccount());
            userCacheDeleteUtil.deleteUserCache(accounts);
        }
    }

    /**
     * 查询初始密码
     */
    @Override
    public String getDefaultPwd() {
        String defaultPwd = sysConfigService.getDefaultPwd();
        return Base64.getEncoder().encodeToString(defaultPwd.getBytes());
    }
}
