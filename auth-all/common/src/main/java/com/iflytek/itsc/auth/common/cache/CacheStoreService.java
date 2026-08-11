package com.iflytek.itsc.auth.common.cache;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Classname CacheStoreService
 * @Description TODO
 * @Date 2024/8/19 15:36
 * @Created by wxqiu
 */
public interface CacheStoreService {

    /**
     *  缓存序列化后的String数据
     *
     * @param key
     * @param value
     */
    void setCacheString(final String key, final String value);

    /**
     *缓存序列化后的String数据
     *
     * @param key 缓存的键值
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
     void setCacheObject(final String key, final String value, final Long timeout, final TimeUnit timeUnit);

    /**
     * 获得缓存的String
     *
     * @param key
     * @return
     */
     String getCacheObject(final String key);

    /**
     * 删除单个对象
     *
     * @param key
     */
     boolean deleteObject(final String key);

    /**
     * 删除集合对象
     *
     * @param keys 多个对象
     * @return
     */
     boolean deleteObject(final List<String> keys);
}
