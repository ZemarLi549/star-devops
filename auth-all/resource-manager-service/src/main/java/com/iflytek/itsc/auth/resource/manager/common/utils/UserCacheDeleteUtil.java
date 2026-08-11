package com.iflytek.itsc.auth.resource.manager.common.utils;

import com.iflytek.itsc.auth.common.cache.CacheStoreService;
import com.iflytek.itsc.auth.common.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname UserCacheDeleteUtil
 * @Description TODO
 * @Date 2024/8/29 16:48
 * @Created by wxqiu
 */
@Component
@Slf4j
public class UserCacheDeleteUtil {

    @Autowired
    private CacheStoreService cacheStoreService;


    public void deleteUserCache(List<String> account) {
        try {
            List<String> prefixAccount = account.stream().map(acc -> CommonConstant.CachePrefix.USER + acc).collect(Collectors.toList());
            cacheStoreService.deleteObject(prefixAccount);
        } catch (Exception e) {
            log.error("UserCacheDeleteUtil|deleteUserCache has Exception:{}", e);
        }

    }
}
