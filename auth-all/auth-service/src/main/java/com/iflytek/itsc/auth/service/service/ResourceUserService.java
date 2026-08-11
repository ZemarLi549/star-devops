package com.iflytek.itsc.auth.service.service;

import com.iflytek.itsc.auth.service.integration.ResourceManagerService;
import com.iflytek.itsc.auth.service.domain.dto.UserPermissionRequest;
import com.iflytek.itsc.auth.common.cache.CacheStoreService;
import com.iflytek.itsc.auth.common.constant.CommonConstant;
import com.iflytek.itsc.auth.resource.manager.domain.dto.ResourceUser;
import com.iflytek.itsc.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RedisUserInfoService
 * @Description 缓存用户信息实现
 * @Date 2024/1/17 15:47
 * @Created by wxqiu
 */
@Component
@Slf4j
public class ResourceUserService {

    @Autowired
    ResourceManagerService resourceManagerService;


    @Autowired
    private CacheStoreService cacheStoreService;

    @Value("${user.expire}")
    private Long expireDay;


    public void saveResourceUserToCache(ResourceUser user) {
        try {
            cacheStoreService.setCacheObject(CommonConstant.CachePrefix.USER + user.getAccount(), JsonUtil.object2Json(user), expireDay, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("saveResourceUserToRedis error:{}", e.getMessage(), e);
        }

    }

    public void deleteResourceUser(String account) {
        cacheStoreService.deleteObject(CommonConstant.CachePrefix.USER + account);
    }

    public Optional<ResourceUser> getResourceUserBaseInfoByAccount(String account) {
        Boolean exceptionFlag = false;
        ResourceUser resourceUser = null;
        try {
            //先从缓存拉取
            resourceUser = JsonUtil.json2Object(cacheStoreService.getCacheObject(CommonConstant.CachePrefix.USER + account), ResourceUser.class);
        } catch (Exception e) {
            //访问redis 异常的情况下，从resource-manager 获取
            log.error("getResourceUserBaseInfoByAccount|getCacheMapValue error:{}", e.getMessage(), e);
            exceptionFlag = true;
        }
        if (resourceUser == null || exceptionFlag) {
            resourceUser = getResourceUserFromResourceManager(account);
            if (resourceUser == null) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(resourceUser);

    }

    private ResourceUser getResourceUserFromResourceManager(String account) {
        UserPermissionRequest userPermissionRequest = new UserPermissionRequest();
        userPermissionRequest.setAccount(account);
        ResourceUser resourceUser = resourceManagerService.getResourceUser(userPermissionRequest);
        if (resourceUser != null) {
            saveResourceUserToCache(resourceUser);
        } else {
            log.info("getResourceUserFromResourceManager is null, account:{}", account);
        }
        return resourceUser;
    }


}
