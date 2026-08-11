package com.iflytek.itsc.auth.common.config;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iflytek.itsc.auth.common.cache.CacheStoreService;
import com.iflytek.itsc.auth.common.cache.LocalCacheStoreService;
import com.iflytek.itsc.auth.common.cache.RedisCacheStoreService;
import com.iflytek.itsc.auth.common.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Classname CacheStoreAutoConfig
 * @Description 缓存类自动配置
 * @Date 2024/8/19 15:36
 * @Created by wxqiu
 */
@Configuration
public class CacheStoreAutoConfig {

    @Value("${user.expire}")
    private Long userExpireDay;

    @Value("${token.expire.access-token-expire}")
    public Long accessTokenExpire;


    @Order(1)
    @Configuration
    @Import({RedisAutoConfiguration.class})
    @AutoConfigureAfter({RedisAutoConfiguration.class})
    public class RedisCacheStoreConfiguration {

        @Bean
        @ConditionalOnProperty(name = "cache.type", havingValue = "redis")
        public CacheStoreService redisCacheStore(StringRedisTemplate redisTemplate) {
            return new RedisCacheStoreService(redisTemplate);
        }

    }

    @Order(2)
    @Configuration
    public class LocalUserCacheStoreConfiguration {

        @Bean
        @ConditionalOnProperty(name = "cache.type", havingValue = "local")
        public CacheStoreService localCacheStore() {
            Map map = new HashMap<String,Cache<String, String>>();
            Cache<String, String> userCache = Caffeine.newBuilder()
                    .expireAfterWrite(userExpireDay, TimeUnit.MINUTES)
                    .build();
            map.put(CommonConstant.CachePrefix.USER,userCache);
            Cache<String, String> tokenCache = Caffeine.newBuilder()
                    .expireAfterWrite(accessTokenExpire, TimeUnit.MINUTES)
                    .build();
            map.put(CommonConstant.CachePrefix.LOGOUT_TOKEN,tokenCache);
            return new LocalCacheStoreService(map);
        }

    }

}
