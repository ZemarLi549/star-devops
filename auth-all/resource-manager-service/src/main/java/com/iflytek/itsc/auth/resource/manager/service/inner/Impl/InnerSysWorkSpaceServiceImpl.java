package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysWorkSpaceBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.KeyLongValueDTO;
import com.iflytek.itsc.auth.resource.manager.domain.dto.SpaceDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysWorkSpaceEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysWorkSpaceSaveForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysWorkspaceParam;
import com.iflytek.itsc.auth.resource.manager.service.base.*;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysWorkSpaceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@Service
public class InnerSysWorkSpaceServiceImpl implements InnerSysWorkSpaceService {
    @Autowired
    private SysWorkSpaceService sysWorkSpaceService;
    @Autowired
    private SysUserWorkSpaceService sysUserWorkSpaceService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysApplicationService sysApplicationService;
    @Autowired
    private SysDataGroupService sysDataGroupService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private UserCacheDeleteUtil userCacheDeleteUtil;

    /**
     * 新增工作空间
     */
    @Override
    @Transactional
    public void insertWorkSpace(SysWorkSpaceSaveForm form) {
        //空间名称唯一性校验
        validateUnique(form.getWorkSpaceName(), null);
        SysWorkSpaceEntity entity = SysWorkSpaceBuilder.buildInsertEntity(form);
        sysWorkSpaceService.save(entity);
    }

    private void validateUnique(String workspaceNm, Long workspaceId){
        Long count = sysWorkSpaceService.lambdaQuery()
                .eq(SysWorkSpaceEntity::getWorkSpaceName, workspaceNm)
                .ne(null != workspaceId, SysWorkSpaceEntity::getWorkSpaceId, workspaceId)
                .count();
        if (count > 0) {
            throw new AuthBizException("空间名称不能重复");
        }
    }

    @Override
    @Transactional
    public void updateWorkSpace(SysWorkSpaceSaveForm form) {
        //空间名称唯一性校验
        validateUnique(form.getWorkSpaceName(), form.getWorkSpaceId());
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (workSpaceEntity == null) {
            throw new AuthBizException("工作空间不存在");
        } else if (!UserInfoContext.isWorkSpaceRelated(workSpaceEntity.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }

        SysWorkSpaceEntity entity = SysWorkSpaceBuilder.buildUpdateEntity(form);
        sysWorkSpaceService.updateById(entity);
    }

    @Override
    @Transactional
    public void deleteWorkSpace(SysWorkSpaceSaveForm form) {
        SysWorkSpaceEntity workSpaceEntity = sysWorkSpaceService.getById(form.getWorkSpaceId());
        if (workSpaceEntity == null) {
            throw new AuthBizException("工作空间不存在");
        } else if (!UserInfoContext.isWorkSpaceRelated(workSpaceEntity.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        } else if (workSpaceEntity.getIsdefault()) {
            throw new AuthBizException("内置工作空间不可删除");
        }


        // 删除用户和空间的关系sys_user_work_space
        sysUserWorkSpaceService.deleteByWorkSpaceId(form.getWorkSpaceId());

        // 删除业务组/数据单元和空间的关系 sys_data_group
        sysDataGroupService.deleteByWorkSpaceId(form.getWorkSpaceId());

        // 删除应用sys_application
        sysApplicationService.deleteByWorkSpaceId(form.getWorkSpaceId());

        // 删除空间下的角色sys_role
        sysRoleService.deleteByWorkSpaceId(form.getWorkSpaceId());
        // 删除用户角色关系sys_user_role
        sysUserRoleService.deleteByWorkSpaceId(form.getWorkSpaceId());

        // 删除空间
        sysWorkSpaceService.removeById(form.getWorkSpaceId());
        ///用户关联的工作空间变更后需要删除缓存信息
        List<String> accounts = sysUserService.listAccountsByWorkSpaceId(form.getWorkSpaceId());
        userCacheDeleteUtil.deleteUserCache(accounts);
    }

    @Override
    public List<SpaceDTO> list(SysWorkspaceParam param) {
        // 查询权限内工作空间id
        Set<Long> spaceIdSet = null;
        if (!UserInfoContext.isSuperAdmin()) {
            spaceIdSet = sysUserWorkSpaceService.listSpaceIdByUserId(UserInfoContext.getUserId());
            if (CollectionUtil.isEmpty(spaceIdSet)) {
                return new ArrayList<>();
            }
        }

        LambdaQueryWrapper<SysWorkSpaceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CollectionUtil.isNotEmpty(spaceIdSet), SysWorkSpaceEntity::getWorkSpaceId, spaceIdSet);
        wrapper.like(StringUtils.isNotBlank(param.getWorkSpaceName()), SysWorkSpaceEntity::getWorkSpaceName, param.getWorkSpaceName());
        wrapper.orderByDesc(SysWorkSpaceEntity::getIsdefault);
        wrapper.orderByAsc(SysWorkSpaceEntity::getWorkSpaceName);
        List<SysWorkSpaceEntity> worklist = sysWorkSpaceService.list(wrapper);
        // 查询用户数量
        Map<Long, Long> workspaceId2UserCountMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(worklist)) {
            List<Long> ids = worklist.stream().map(SysWorkSpaceEntity::getWorkSpaceId).collect(Collectors.toList());
            List<SysUserWorkSpaceEntity> userWorkSpaceEntities = sysUserWorkSpaceService.listByWorkSpaceIds(ids);
            workspaceId2UserCountMap = userWorkSpaceEntities.stream()
                    .collect(Collectors.groupingBy(SysUserWorkSpaceEntity::getWorkSpaceId,
                            Collectors.mapping(SysUserWorkSpaceEntity::getUserId, Collectors.counting())));
        }

        // 转换对象
        List<SpaceDTO> list = new ArrayList<>();
        for (SysWorkSpaceEntity entity : worklist) {
            SpaceDTO dto = BeanUtil.copyProperties(entity, SpaceDTO.class);
            dto.setCount(workspaceId2UserCountMap.getOrDefault(dto.getWorkSpaceId(), 0L));
            list.add(dto);
        }
        return list;
    }

    /**
     * 全量的空间列表下拉
     */
    @Override
    public List<KeyLongValueDTO> listTotalDrown() {
        List<SysWorkSpaceEntity> list = sysWorkSpaceService.lambdaQuery()
                .select(SysWorkSpaceEntity::getWorkSpaceId, SysWorkSpaceEntity::getWorkSpaceName)
                .orderByDesc(SysWorkSpaceEntity::getIsdefault)
                .orderByAsc(SysWorkSpaceEntity::getWorkSpaceName)
                .list();
        return list.stream()
                .map(s -> new KeyLongValueDTO(s.getWorkSpaceId(), s.getWorkSpaceName()))
                .collect(Collectors.toList());
    }
}
