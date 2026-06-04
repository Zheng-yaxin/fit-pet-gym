package com.gym.common.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 内存缓存实现（替代 Redis）
 * 注意：
 * 1. 数据保存在 JVM 内存中，重启服务会导致所有用户需要重新登录。
 * 2. 此简单实现未处理过期时间（Timeout），数据会一直存在直到重启或手动删除。
 */
@Component
@SuppressWarnings(value = { "unchecked", "rawtypes" })
public class RedisCache {

    // 使用静态 Map 模拟 Redis 存储
    public static final Map<String, Object> CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 缓存基本的对象
     */
    public <T> void setCacheObject(final String key, final T value) {
        CACHE_MAP.put(key, value);
    }

    /**
     * 缓存基本的对象（带过期时间）
     * 注意：此内存版实现忽略了 timeout，数据不会自动过期
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        CACHE_MAP.put(key, value);
    }

    /**
     * 设置有效时间 (模拟接口，实际不做操作)
     */
    public boolean expire(final String key, final long timeout) {
        return true;
    }

    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return true;
    }

    /**
     * 获得缓存的基本对象
     */
    public <T> T getCacheObject(final String key) {
        return (T) CACHE_MAP.get(key);
    }

    /**
     * 删除单个对象
     */
    public boolean deleteObject(final String key) {
        return CACHE_MAP.remove(key) != null;
    }

    /**
     * 获得缓存的list对象
     */
    public <T> List<T> getCacheList(final String key) {
        Object o = CACHE_MAP.get(key);
        if (o instanceof List) {
            return (List<T>) o;
        }
        return new ArrayList<>();
    }

    /**
     * 缓存List数据
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        CACHE_MAP.put(key, dataList);
        return dataList.size();
    }

    /**
     * 获得缓存的Map
     */
    public <T> Map<String, T> getCacheMap(final String key) {
        Object o = CACHE_MAP.get(key);
        if (o instanceof Map) {
            return (Map<String, T>) o;
        }
        return null;
    }

    /**
     * 缓存Map
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            CACHE_MAP.put(key, dataMap);
        }
    }
}