package com.iflytek.itsc.auth.resource.manager.service.inner.Impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iflytek.itsc.auth.common.util.UserInfoContext;
import com.iflytek.itsc.auth.resource.manager.common.exception.AuthBizException;
import com.iflytek.itsc.auth.resource.manager.domain.builder.SysApplicationBuilder;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ApplicationDTO;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysApplicationEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysDataGroupEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserEntity;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationDeleteForm;
import com.iflytek.itsc.auth.resource.manager.domain.form.SysApplicationSaveForm;
import com.iflytek.itsc.auth.resource.manager.domain.vo.SysApplicationPageParam;
import com.iflytek.itsc.auth.resource.manager.service.base.SysApplicationService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysDataGroupService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserService;
import com.iflytek.itsc.auth.resource.manager.service.inner.InnerSysApplicationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2023/12/13
 * @desc
 **/
@Service
public class InnerSysApplicationServiceImpl implements InnerSysApplicationService {
    @Autowired
    private SysApplicationService sysApplicationService;
    @Autowired
    private SysDataGroupService sysDataGroupService;
    @Autowired
    private SysUserService sysUserService;
    @Value("${app-name-regex:^[a-zA-Z0-9_-]+$}")
    private String appNameRegex;

    @Override
    @Transactional
    public void insert(SysApplicationSaveForm form) {
        // 合法性校验
        form.setApplicationId(null);
        this.validateLegal(form);

        // 保存应用
        SysApplicationEntity entity = SysApplicationBuilder.buildApplicationInsertEntity(form);
        sysApplicationService.save(entity);
    }

    @Override
    @Transactional
    public void update(SysApplicationSaveForm form) {
        // 合法性校验
        this.validateLegal(form);

        // 更新业务组
        SysApplicationEntity entity = SysApplicationBuilder.buildApplicationUpdateEntity(form);
        sysApplicationService.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(SysApplicationDeleteForm form) {
        List<SysApplicationEntity> list = sysApplicationService.listByIds(form.getApplicationId());
        if (list.size() != form.getApplicationId().size()) {
            throw new AuthBizException("应用不存在");
        } else {
            for (SysApplicationEntity applicationEntity : list) {
                if (!UserInfoContext.isWorkSpaceRelated(applicationEntity.getWorkSpaceId())) {
                    throw new AuthBizException("无工作空间权限");
                }
            }
        }
        sysApplicationService.deleteByIds(form.getApplicationId());
    }

    @Override
    public Page<ApplicationDTO> page(SysApplicationPageParam form) {
        if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }

        LambdaQueryWrapper<SysApplicationEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysApplicationEntity::getWorkSpaceId, form.getWorkSpaceId());
        wrapper.eq(SysApplicationEntity::getIsvalid, true);
        wrapper.eq(null != form.getDataGroupId(), SysApplicationEntity::getDataGroupId, form.getDataGroupId());
        wrapper.like(StringUtils.isNotBlank(form.getName()), SysApplicationEntity::getApplicationName, escapeSpecialCharacter(form.getName()));
        wrapper.orderByDesc(SysApplicationEntity::getCreateTime);
        Page<SysApplicationEntity> queryPage = new Page<>(form.getPageNo(), form.getPageSize());
        queryPage = sysApplicationService.page(queryPage, wrapper);

        Page<ApplicationDTO> page = new Page<>();
        BeanUtil.copyProperties(queryPage, page);
        if (CollectionUtil.isNotEmpty(queryPage.getRecords())) {
            List<Long> userIds = queryPage.getRecords().stream().map(SysApplicationEntity::getCreateUser).collect(Collectors.toList());
            // 查询用户名字
            List<SysUserEntity> sysApplicationEntities = sysUserService.lambdaQuery()
                    .in(SysUserEntity::getUserId, userIds)
                    .list();
            Map<Long, String> userNameMap = sysApplicationEntities.stream().collect(
                    Collectors.toMap(SysUserEntity::getUserId, SysUserEntity::getAccount));

            List<ApplicationDTO> list = new ArrayList<>(queryPage.getRecords().size());
            for (SysApplicationEntity record : queryPage.getRecords()) {
                ApplicationDTO applicationDTO = BeanUtil.copyProperties(record, ApplicationDTO.class);
                applicationDTO.setCreateUserName(userNameMap.getOrDefault(applicationDTO.getCreateUser(), ""));
                list.add(applicationDTO);
            }
            page.setRecords(list);
        }
        return page;
    }

    private static String escapeSpecialCharacter(String originalStr) {
        if (StringUtils.isNotBlank(originalStr)) {
            originalStr = originalStr.replaceAll("\\\\", "\\\\\\\\");
            originalStr = originalStr.replaceAll("_", "\\\\_");
            originalStr = originalStr.replaceAll("%", "\\\\%");
        }
        return originalStr;
    }

    private void validateLegal(SysApplicationSaveForm form) {
        if (!UserInfoContext.isWorkSpaceRelated(form.getWorkSpaceId())) {
            throw new AuthBizException("无工作空间权限");
        }
        // 应用名称纯英文和数字
        if (!form.getApplicationName().matches(appNameRegex)) {
            throw new AuthBizException("应用名称正则校验失败");
        }
        SysDataGroupEntity dataGroupEntity = sysDataGroupService.getById(form.getDataGroupId());
        if (dataGroupEntity == null) {
            throw new AuthBizException("数据单元不存在");
        } else if (!dataGroupEntity.getWorkSpaceId().equals(form.getWorkSpaceId())) {
            throw new AuthBizException("数据单元与工作空间不匹配");
        }
        if (form.getApplicationId() != null) {
            SysApplicationEntity applicationEntity = sysApplicationService.getById(form.getApplicationId());
            if (applicationEntity == null) {
                throw new AuthBizException("应用不存在");
            } else if (!applicationEntity.getDataGroupId().equals(form.getDataGroupId())) {
                throw new AuthBizException("应用与数据单元不匹配");
            }
        }
        // 空间下应用名称唯一
        Long existCount = sysApplicationService.lambdaQuery()
                .eq(SysApplicationEntity::getWorkSpaceId, form.getWorkSpaceId())
                .eq(SysApplicationEntity::getApplicationName, form.getApplicationName())
                .ne(form.getApplicationId() != null, SysApplicationEntity::getApplicationId, form.getApplicationId())
                .count();
        if (existCount > 0) {
            throw new AuthBizException("应用名称空间下已存在");
        }
    }
}
