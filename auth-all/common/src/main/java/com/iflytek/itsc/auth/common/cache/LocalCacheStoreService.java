package com.iflytek.itsc.auth.common.cache;

import com.github.benmanes.caffeine.cache.Cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Classname LocalCacheStoreService
 * @Description TODO
 * @Date 2024/8/19 15:43
 * @Created by wxqiu
 */
public class LocalCacheStoreService implements CacheStoreService {

    protected Map<String, Cache<String, String>> cacheMap;

    public LocalCacheStoreService(Map<String, Cache<String, String>> cacheMap1) {
        cacheMap = cacheMap1;
    }

    @Override
    public void setCacheString(String key, String value) {
        Cache<String, String> cache = getCacheMap(key);
        cache.put(key, value);
    }

    @Override
    public void setCacheObject(final String key, final String value, final Long timeout, final TimeUnit timeUnit) {
        Cache<String, String> cache = getCacheMap(key);
        cache.put(key, value);
    }

    @Override
    public String getCacheObject(String key) {
        Cache<String, String> cache = getCacheMap(key);
        return cache.getIfPresent(key);
    }

    @Override
    public boolean deleteObject(String key) {
        Cache<String, String> cache = getCacheMap(key);
        cache.invalidate(key);
        return true;
    }

    @Override
    public boolean deleteObject(List<String> keys) {
        Cache<String, String> cache = getCacheMap(keys.get(0));
        cache.invalidateAll(keys);
        return true;
    }


    private Cache<String, String> getCacheMap(String key) {
        String prefix = key.substring(0, key.indexOf("_") + 1);
        return cacheMap.get(prefix);
    }
}
