package com.iflytek.itsc.auth.common.cache;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Classname RedisCacheStoreService
 * @Description redis 缓存实现
 * @Date 2024/8/19 15:36
 * @Created by wxqiu
 */
public class RedisCacheStoreService implements CacheStoreService {

    protected StringRedisTemplate redisTemplate;

    public RedisCacheStoreService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setCacheString(final String key, final String value) {
        redisTemplate.opsForValue().set(key, value);
    }


    @Override
    public  void setCacheObject(final String key, final String value, final Long timeout, final TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    @Override
    public String getCacheObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }



    @Override
    public boolean deleteObject(final List<String> keys) {
        return redisTemplate.delete(keys) > 0;
    }

}
