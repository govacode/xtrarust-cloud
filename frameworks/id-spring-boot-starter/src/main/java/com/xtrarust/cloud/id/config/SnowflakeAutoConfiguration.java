package com.xtrarust.cloud.id.config;

import com.xtrarust.cloud.id.core.provider.ManualSnowflakeNodeIdProvider;
import com.xtrarust.cloud.id.core.provider.RandomSnowflakeNodeIdProvider;
import com.xtrarust.cloud.id.core.provider.RedisSnowflakeNodeIdProvider;
import com.xtrarust.cloud.id.core.provider.SnowflakeNodeIdProvider;
import com.xtrarust.cloud.id.core.snowflake.Snowflake;
import com.xtrarust.cloud.redis.config.RedisAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 雪花{@code ID}自动装配
 *
 * @author gova
 */
@Slf4j
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "snowflake.node-id-provider-type", havingValue = "manual")
    public ManualSnowflakeNodeIdProvider manualSnowflakeNodeIdProvider(SnowflakeProperties properties) {
        return new ManualSnowflakeNodeIdProvider(properties.getManualDataCenterId(), properties.getManualWorkerId());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "snowflake.node-id-provider-type", havingValue = "random", matchIfMissing = true)
    public RandomSnowflakeNodeIdProvider randomSnowflakeNodeIdProvider() {
        return new RandomSnowflakeNodeIdProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnProperty(name = "snowflake.node-id-provider-type", havingValue = "redis")
    public RedisSnowflakeNodeIdProvider redisSnowflakeNodeIdProvider(SnowflakeProperties properties,
                                                                     StringRedisTemplate stringRedisTemplate) {
        return new RedisSnowflakeNodeIdProvider(properties.getRedisKey(), stringRedisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public Snowflake snowflake(SnowflakeNodeIdProvider provider,
                               SnowflakeProperties properties) {
        Pair<Long, Long> nodeIdPair = provider.getNodeIdPair();
        long dataCenterId = nodeIdPair.getLeft(), workerId = nodeIdPair.getRight();
        log.info("Snowflake dataCenterId: {}, workerId: {}, maxVibrationOffset: {}, maxTolerateTimeDifferenceMillis: {}",
                dataCenterId, workerId, properties.getMaxVibrationOffset(), properties.getMaxTolerateTimeDifferenceMillis());
        return new Snowflake(dataCenterId, workerId, properties.getMaxVibrationOffset(), properties.getMaxTolerateTimeDifferenceMillis());
    }
}
