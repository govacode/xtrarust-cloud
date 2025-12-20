package com.xtrarust.cloud.sms.dao;

import org.dromara.sms4j.api.dao.SmsDao;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * SmsDao缓存
 * <p>主要用于短信重试和拦截的缓存
 *
 * @author Feng
 */
public class RedisSmsDao implements SmsDao {

    private static final String SMS_PREFIX = "sms:";

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisSmsDao(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 存储
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间（单位：秒)
     */
    @Override
    public void set(String key, Object value, long cacheTime) {
        redisTemplate.opsForValue().set(SMS_PREFIX + key, value, Duration.ofSeconds(cacheTime));
    }

    /**
     * 存储
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(SMS_PREFIX + key, value);
    }

    /**
     * 读取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(SMS_PREFIX + key);
    }

    /**
     * remove
     * <p> 根据key移除缓存
     *
     * @param key 缓存键
     * @return 被删除的value
     * @author :Wind
     */
    @Override
    public Object remove(String key) {
        return redisTemplate.delete(SMS_PREFIX + key);
    }

    /**
     * 清空
     */
    @Override
    public void clean() {
        throw new UnsupportedOperationException();
    }

}
