package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.common.utils.DozerBeanUtil;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysWorkSpaceUserBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.WorkSpaceUserDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.WorkSpaceUserDetailDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.*;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceUserForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysWorkSpaceUserQueryParam;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysWorkSpaceUserService;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/19
 * @desc
 **/
@Service
public class InnerSysWorkSpaceUserServiceImpl implements InnerSysWorkSpaceUserService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysWorkSpaceService sysWorkSpaceService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserWorkSpaceService sysUserWorkSpaceService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private UserCacheDeleteUtil userCacheDeleteUtil;

    /**
     * 添加成员
     */
    @Override
    @Transactional
    public void saveOrUpdate(SysWorkSpaceUserForm form) {
        this.validateLegal(form);

        // 待保存用户空间关系
        SysUserWorkSpaceEntity userWorkSpaceEntity = SysWorkSpaceUserBuilder.buildUserWorkSpaceEntity(form);
        // 待保存用户角色关系
        SysUserRoleEntity userRoleEntity = SysWorkSpaceUserBuilder.buildUserRoleEntity(form);

        // 保存用户空间关系
        sysUserWorkSpaceService.deleteBy(form.getUserId(), form.getWorkSpaceId());
        sysUserWorkSpaceService.save(userWorkSpaceEntity);
        // 保存用户角色
        sysUserRoleService.deleteBy(form.getUserId(), form.getWorkSpaceId());
        sysUserRoleService.save(userRoleEntity);
        //用户关联的工作空间变更后需要删除缓存信息
        List<String> accounts = sysUserService.listAccountByUserIds(new ArrayList<>(Collections.singletonList(form.getUserId())));
        userCacheDeleteUtil.deleteUserCache(accounts);

    }

    /**
     * 表单合法校验
     */
    private void validateLegal(SysWorkSpaceUserForm form) {
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (workSpaceEntity == null) {
            throw new AuthBizException("工作空间不存在");
        } else if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        SysUserEntity userEntity = sysUserService.getById(form.getUserId());
        if (userEntity == null) {
            throw new AuthBizException("用户不存在");
        }
        SysRoleEntity roleEntity = sysRoleService.getById(form.getRoleId());
        if (roleEntity == null) {
            throw new AuthBizException("角色不存在");
        }
        if (!roleEntity.getIsdefault() && !form.getWorkSpaceId().equals(roleEntity.getWorkSpaceId())) {
            throw new AuthBizException("角色和工作空间不匹配");
        }
    }

    /**
     * 分页查询
     */
    @Override
    public Page<WorkSpaceUserDTO> page(SysWorkSpaceUserQueryParam param) {
        if (!UserInfoContext.isWorkSpaceRelated(param.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }

        // 先查询用户ids
        List<Long> userIds = null;
        if (StringUtils.isNotBlank(param.getNickName())) {
            List<SysUserEntity> list = sysUserService.lambdaQuery()
                    .eq(SysUserEntity::getIsactive, true)
                    .and(s -> s.like(SysUserEntity::getNickName, param.getNickName()).or().like(SysUserEntity::getAccount, param.getNickName()))
                    .select(SysUserEntity::getUserId)
                    .list();
            if (CollectionUtil.isEmpty(list)) {
                return new Page<>(param.getPageNo(), param.getPageSize());
            }
            userIds = list.stream().map(SysUserEntity::getUserId).collect(Collectors.toList());
        }
        // 分页查询
        LambdaQueryWrapper<SysUserWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserWorkSpaceEntity::getWorkSpaceId, param.getWorkSpaceId());
        wrapper.in(userIds != null, SysUserWorkSpaceEntity::getUserId, userIds);
        wrapper.orderByDesc(SysUserWorkSpaceEntity::getCreateTime);
        Page<SysUserWorkSpaceEntity> queryPage = new Page<>(param.getPageNo(), param.getPageSize());
        queryPage = sysUserWorkSpaceService.page(queryPage, wrapper);
        Page<WorkSpaceUserDTO> page = DozerBeanUtil.convert(queryPage, WorkSpaceUserDTO.class);

        // 封装结果
        if (CollectionUtil.isNotEmpty(queryPage.getRecords())) {
            List<Long> userIdList = queryPage.getRecords().stream().map(SysUserWorkSpaceEntity::getUserId).collect(Collectors.toList());
            List<SysUserEntity> userEntities = sysUserService.listByIds(userIdList);
            Map<Long, SysUserEntity> userId2EntityMap = userEntities.stream().collect(Collectors.toMap(SysUserEntity::getUserId, s -> s));
            // 查询用户角色
            Map<Long, String> userId2RoleName = this.getUserId2RoleName(param.getWorkSpaceId(), userIdList);
            // 创建/更新时间
            Map<Long, Date> userId2CreateTimeMap = queryPage.getRecords().stream().collect(Collectors.toMap(SysUserWorkSpaceEntity::getUserId, SysUserWorkSpaceEntity::getCreateTime));

            List<WorkSpaceUserDTO> dtoList = new ArrayList<>();
            for (Long userId : userIdList) {
                SysUserEntity userEntity = userId2EntityMap.get(userId);
                if (userEntity == null) continue;
                WorkSpaceUserDTO dto = new WorkSpaceUserDTO();
                dto.setUserId(userEntity.getUserId());
                dto.setAccount(userEntity.getAccount());
                dto.setEmail(userEntity.getEmail());
                dto.setNickName(userEntity.getNickName());
                dto.setPhone(userEntity.getPhone());
                dto.setRoleName(userId2RoleName.getOrDefault(dto.getUserId(), "-"));
                dto.setCreateTime(userId2CreateTimeMap.containsKey(userEntity.getUserId())
                        ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(userId2CreateTimeMap.get(userEntity.getUserId()))
                        : "-");
                dtoList.add(dto);
            }
            page.setRecords(dtoList);
        }

        return page;
    }

    /**
     * 查询用户角色名称
     */
    private Map<Long, String> getUserId2RoleName(Long workSpaceId, List<Long> userIdList) {
        // 查询用户角色关系
        List<SysUserRoleEntity> list = sysUserRoleService.lambdaQuery()
                .eq(SysUserRoleEntity::getWorkSpaceId, workSpaceId)
                .in(SysUserRoleEntity::getUserId, userIdList)
                .list();
        if (CollectionUtil.isEmpty(list)) return new HashMap<>();

        Map<Long, Set<Long>> userId2RoleIdsMap = list.stream()
                .collect(Collectors.groupingBy(SysUserRoleEntity::getUserId,
                        Collectors.mapping(SysUserRoleEntity::getRoleId, Collectors.toSet())));
        Set<Long> roleIds = list.stream().map(SysUserRoleEntity::getRoleId).collect(Collectors.toSet());

        // 查询角色
        List<SysRoleEntity> roleEntities = sysRoleService.listByIds(roleIds);
        Map<Long, String> roleId2NameMap = roleEntities.stream().collect(Collectors.toMap(SysRoleEntity::getRoleId, SysRoleEntity::getRoleName));

        // 组装用户角色名称
        return userId2RoleIdsMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, s ->
                        s.getValue().stream()
                                .map(roleId2NameMap::get)
                                .filter(StringUtils::isNotBlank)
                                .collect(Collectors.joining(","))));
    }

    /**
     * 批量移除
     */
    @Override
    @Transactional
    public void batchRemove(SysWorkSpaceUserQueryParam param) {
        if (CollectionUtil.isEmpty(param.getUserIdList()) || param.getWorkSpaceId() == null) {
            throw new AuthBizException("用户id列表和工作空间id不能为空");
        }
        if (!UserInfoContext.isWorkSpaceRelated(param.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        // 删除角色关系
        sysUserRoleService.deleteBySpaceIdUserIds(param.getWorkSpaceId(), param.getUserIdList());
        // 删除工作空间关系
        sysUserWorkSpaceService.deleteBySpaceIdUserIds(param.getWorkSpaceId(), param.getUserIdList());
        //用户关联的工作空间变更后需要删除缓存信息
        List<String> accounts = sysUserService.listAccountByUserIds(param.getUserIdList());
        userCacheDeleteUtil.deleteUserCache(accounts);

    }

    /**
     * 查询用户下拉列表
     */
    @Override
    public List<KeyValueDTO> listUserDrown(Long workSpaceId, String nickName) {
        LambdaQueryWrapper<SysUserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserEntity::getIsvalid, true);
        if (StringUtils.isNotBlank(nickName)) {
            wrapper.and(s -> s.like(SysUserEntity::getAccount, nickName).or().like(SysUserEntity::getNickName, nickName));
        }
        wrapper.orderByDesc(SysUserEntity::getAccount);
        wrapper.select(SysUserEntity::getUserId, SysUserEntity::getAccount, SysUserEntity::getNickName);
        List<SysUserEntity> list = sysUserService.list(wrapper);

        // 查询空间全部成员
        Set<Long> relatedUserIds = new HashSet<>();
        if (workSpaceId != null) {
            List<SysUserWorkSpaceEntity> entities = sysUserWorkSpaceService.listByWorkSpaceIds(Collections.singletonList(workSpaceId));
            relatedUserIds = entities.stream().map(SysUserWorkSpaceEntity::getUserId).collect(Collectors.toSet());
        }

        List<KeyValueDTO> dtoList = new ArrayList<>(list.size());
        for (SysUserEntity entity : list) {
            KeyValueDTO dto = new KeyValueDTO();
            dto.setKey(String.valueOf(entity.getUserId()));
            dto.setValue(entity.getNickName() + "(" + entity.getAccount() + ")");
            dto.setIsselect(relatedUserIds.contains(entity.getUserId()));
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public WorkSpaceUserDetailDTO getDetail(Long workSpaceId, Long userId) {
        if (workSpaceId == null || userId == null) {
            throw new AuthBizException("参数不能为空");
        }
        if (!UserInfoContext.isWorkSpaceRelated(workSpaceId)) {
            throw new AuthBizException("无工作空间权限");
        }

        WorkSpaceUserDetailDTO dto = new WorkSpaceUserDetailDTO();
        // 查询工作空间名称
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(workSpaceId);
        dto.setWorkSpaceName(workSpaceEntity.getWorkSpaceName());
        // 查询用户信息
        SysUserEntity userEntity = sysUserService.getById(userId);
        dto.setUserId(userId);
        dto.setPhone(userEntity.getPhone());
        dto.setEmail(userEntity.getEmail());
        dto.setNickName(userEntity.getNickName());
        dto.setAccount(userEntity.getAccount());
        // 查询用户角色
        List<Long> roleIds = sysUserRoleService.listRoleIdsBy(userId, workSpaceId);
        if (CollectionUtil.isNotEmpty(roleIds)) {
            dto.setRoleId(roleIds.get(0));
            SysRoleEntity roleEntity = sysRoleService.getById(dto.getRoleId());
            dto.setRoleName(roleEntity.getRoleName());
        }
        return dto;
    }
}
