package com.iflytek.itsc.auth.resource.manager.common.init;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.iflytek.itsc.auth.resource.manager.common.constants.Constant;
import com.iflytek.itsc.auth.resource.manager.common.utils.MD5Util;
import com.iflytek.itsc.auth.resource.manager.common.utils.UserCacheDeleteUtil;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysRoleEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserEntity;
import com.iflytek.itsc.auth.resource.manager.domain.entity.SysUserRoleEntity;
import com.iflytek.itsc.auth.resource.manager.service.base.SysConfigService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysRoleService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserRoleService;
import com.iflytek.itsc.auth.resource.manager.service.base.SysUserService;
import com.iflytek.itsc.util.json.JsonUtil;
import com.iflytek.itsc.util.security.HMacUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xdkong2
 * @date 2024/1/28
 * @desc 初始化超管用户及超管关系
 **/
@Component
@Slf4j
public class InitAdmin {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    UserCacheDeleteUtil userCacheDeleteUtil;


    /**
     * 超管账号
     */
    @Value("${init-admin-account:admin}")
    private String adminAccount;

    @PostConstruct
    private void init() {
        log.info("--------初始化admin start-------");
        if (StringUtils.isBlank(adminAccount)) {
            log.info("超管账号为空，放弃关联");
            return;
        }
        List<String> accounts = Arrays.asList(adminAccount.split(","));
        log.info("配置account: {}", JsonUtil.object2Json(accounts));
        // 查询默认密码
        String defaultPwd = HMacUtil.hMacMd5(MD5Util.getMD5(sysConfigService.getDefaultPwd()).toLowerCase());

        // 不存在则创建用户
        Set<Long> userIdSet = new HashSet<>();
        List<SysUserEntity> oldUserList = sysUserService.lambdaQuery().in(SysUserEntity::getAccount, accounts).list();
        Map<String, Long> oldAccount2UserIdMap = oldUserList.stream().collect(Collectors.toMap(SysUserEntity::getAccount, SysUserEntity::getUserId));
        for (String account : accounts) {
            if (oldAccount2UserIdMap.containsKey(account)) {
                log.info("用户已存在, account: {}", account);
                userIdSet.add(oldAccount2UserIdMap.get(account));
                continue;
            }
            SysUserEntity userEntity = new SysUserEntity();
            userEntity.setAccount(account);
            userEntity.setNickName(account);
            userEntity.setPasswd(defaultPwd);
            sysUserService.save(userEntity);
            userIdSet.add(userEntity.getUserId());
            log.info("创建用户成功, account: {}", account);
        }

        // 查询超管角色
        SysRoleEntity roleEntity = sysRoleService.lambdaQuery().eq(SysRoleEntity::getIssuperadmin, true).last("limit 1").one();
        if (roleEntity == null) {
            log.info("没有超管角色");
            return;
        }
        Long adminRoleId = roleEntity.getRoleId();

        // 创建超管关系，多余的删除，没有的增加
        List<SysUserRoleEntity> oldUserRoleList = sysUserRoleService.lambdaQuery().eq(SysUserRoleEntity::getRoleId, adminRoleId).list();
        Set<Long> oldAdminUserIdSet = oldUserRoleList.stream().map(SysUserRoleEntity::getUserId).collect(Collectors.toSet());
        List<Long> todoDeleteRelUserIds = oldAdminUserIdSet.stream().filter(s -> !userIdSet.contains(s)).collect(Collectors.toList());
        List<Long> todoInsertRelUserIds = userIdSet.stream().filter(s -> !oldAdminUserIdSet.contains(s)).collect(Collectors.toList());

        if (!todoDeleteRelUserIds.isEmpty()) {
            LambdaQueryWrapper<SysUserRoleEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(SysUserRoleEntity::getUserId, todoDeleteRelUserIds);
            wrapper.eq(SysUserRoleEntity::getRoleId, adminRoleId);
            sysUserRoleService.remove(wrapper);
            log.info("移除超管关系，userId：{}", JsonUtil.object2Json(todoDeleteRelUserIds));
        }

        if (!todoInsertRelUserIds.isEmpty()) {
            List<SysUserRoleEntity> userRoleEntities = new ArrayList<>();
            for (Long userId : todoInsertRelUserIds) {
                SysUserRoleEntity entity = new SysUserRoleEntity();
                entity.setUserId(userId);
                entity.setRoleId(adminRoleId);
                entity.setWorkSpaceId(Constant.DEFAULT_PARENT_ID);
                userRoleEntities.add(entity);
            }
            sysUserRoleService.saveBatch(userRoleEntities);
            log.info("增加超管关系，userId：{}", JsonUtil.object2Json(todoInsertRelUserIds));
        }

        // 查询关联用户，并推送auth变更
        userIdSet.addAll(oldAdminUserIdSet);
        List<SysUserEntity> userEntities = sysUserService.listByIds(userIdSet);
        List<String> pushAccounts = userEntities.stream().map(SysUserEntity::getAccount).collect(Collectors.toList());
        userCacheDeleteUtil.deleteUserCache(pushAccounts);

        log.info("--------初始化admin end-------");
    }
}
