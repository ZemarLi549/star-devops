package com.iflytek.itsc.auth.service.common.config;

import cloud.tianai.captcha.spring.store.CacheStore;
import cloud.tianai.captcha.spring.store.impl.LocalCacheStore;
import cloud.tianai.captcha.spring.store.impl.RedisCacheStore;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @Classname ImageCaptchaAutoConfig
 * @Description 图片验证码配置 原starter 插件自动配置不支持，所以做了自定义
 * @Date 2024/8/23 17:02
 * @Created by wxqiu
 */
@Configuration
public class ImageCaptchaAutoConfig {

    @Order(1)
    @Configuration
    @Import({RedisAutoConfiguration.class})
    @AutoConfigureAfter({RedisAutoConfiguration.class})
    public static class RedisCacheStoreConfiguration {

        @Bean
        @ConditionalOnProperty(name = "cache.type", havingValue = "redis")
        public CacheStore redis(StringRedisTemplate redisTemplate) {
            return new RedisCacheStore(redisTemplate);
        }
    }

    /**
     * @Author: 天爱有情
     * @date 2020/10/27 14:06
     * @Description LocalCacheSliderCaptchaApplication
     */
    @Order(2)
    @Configuration
    public static class LocalCacheStoreConfiguration {

        @Bean
        @ConditionalOnProperty(name = "cache.type", havingValue = "local")
        public CacheStore local() {
            return new LocalCacheStore();
        }
    }
}
