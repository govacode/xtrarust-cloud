package com.xtrarust.cloud.id.config;

import com.xtrarust.cloud.id.core.snowflake.RedisSnowflakeInitializer;
import com.xtrarust.cloud.id.core.snowflake.RandomSnowflakeInitializer;
import com.xtrarust.cloud.redis.config.CloudRedisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 分布式 ID 自动装配
 */
@AutoConfiguration(after = CloudRedisAutoConfiguration.class)
public class DistributedIdAutoConfiguration {

    /**
     * 本地 Redis 构建雪花 WorkId 选择器
     */
    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    public RedisSnowflakeInitializer redisSnowflakeInitializer(StringRedisTemplate stringRedisTemplate) {
        return new RedisSnowflakeInitializer(stringRedisTemplate);
    }

    /**
     * 随机数构建雪花 WorkId 选择器。如果项目未使用 Redis，使用该选择器
     */
    @Bean
    @ConditionalOnMissingBean(RedisSnowflakeInitializer.class)
    public RandomSnowflakeInitializer randomSnowflakeInitializer() {
        return new RandomSnowflakeInitializer();
    }
}
