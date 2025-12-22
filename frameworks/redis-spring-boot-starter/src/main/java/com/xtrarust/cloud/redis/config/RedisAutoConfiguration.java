package com.xtrarust.cloud.redis.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.xtrarust.cloud.common.util.StringUtils;
import com.xtrarust.cloud.redis.lock.DistributedLock;
import com.xtrarust.cloud.redis.lock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.NameMapper;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.ConstantDelay;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Redis 配置类<br>
 * <p>
 * redisson配置使用参考：<a href="https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter">redisson-spring-boot-starter</a>
 *
 * @see org.redisson.spring.starter.RedissonAutoConfiguration
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisAutoConfiguration {

    @Bean
    public RedissonAutoConfigurationCustomizer redissonAutoConfigurationCustomizer(ObjectMapper objectMapper,
                                                                                   RedissonProperties redissonProperties) {
        return configuration -> {
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, objectMapper);
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            // 通用配置
            configuration
                    .setLazyInitialization(redissonProperties.getLazyInitialization())
                    .setThreads(redissonProperties.getThreads())
                    .setNettyThreads(redissonProperties.getNettyThreads())
                    .setTransportMode(redissonProperties.getTransportMode())
                    .setProtocol(redissonProperties.getProtocol())
                    .setLockWatchdogTimeout(redissonProperties.getLockWatchdogTimeout())
                    .setCheckLockSyncedSlaves(redissonProperties.getCheckLockSyncedSlaves())
                    .setSlavesSyncTimeout(redissonProperties.getSlavesSyncTimeout())
                    .setReliableTopicWatchdogTimeout(redissonProperties.getReliableTopicWatchdogTimeout())
                    .setUseScriptCache(redissonProperties.getUseScriptCache())
                    .setKeepPubSubOrder(redissonProperties.getKeepPubSubOrder())
                    .setMinCleanUpDelay(redissonProperties.getMinCleanUpDelay())
                    .setMaxCleanUpDelay(redissonProperties.getMaxCleanUpDelay())
                    .setCleanUpKeysAmount(redissonProperties.getCleanUpKeysAmount())
                    .setCodec(codec);
            // 集群配置
            RedissonProperties.ClusterConfig clusterConfig = redissonProperties.getCluster();
            if (ObjectUtil.isNotNull(clusterConfig)) {
                configuration.useClusterServers()
                        // 设置redis key前缀
                        .setNameMapper(new KeyPrefixNameMapper(redissonProperties.getKeyPrefix()))
                        .setCheckSlotsCoverage(clusterConfig.getCheckSlotsCoverage())
                        // 集群拓扑扫描
                        .setScanInterval(clusterConfig.getScanInterval())
                        .setReadMode(clusterConfig.getReadMode())
                        .setSubscriptionMode(clusterConfig.getSubscriptionMode())
                        .setSubscriptionConnectionMinimumIdleSize(clusterConfig.getSubscriptionConnectionMinimumIdleSize())
                        .setSubscriptionConnectionPoolSize(clusterConfig.getSubscriptionConnectionPoolSize())
                        .setShardedSubscriptionMode(clusterConfig.getShardedSubscriptionMode())
                        .setSlaveConnectionMinimumIdleSize(clusterConfig.getSlaveConnectionMinimumIdleSize())
                        .setSlaveConnectionPoolSize(clusterConfig.getSlaveConnectionPoolSize())
                        .setMasterConnectionMinimumIdleSize(clusterConfig.getMasterConnectionMinimumIdleSize())
                        .setMasterConnectionPoolSize(clusterConfig.getMasterConnectionPoolSize())
                        .setIdleConnectionTimeout(clusterConfig.getIdleConnectionTimeout())
                        .setRetryAttempts(clusterConfig.getRetryAttempts())
                        .setRetryDelay(new ConstantDelay(Duration.ofMillis(clusterConfig.getRetryInterval())));
            }
            // 哨兵配置
            RedissonProperties.SentinelConfig sentinelConfig = redissonProperties.getSentinel();
            if (ObjectUtil.isNotNull(sentinelConfig)) {
                // TODO
            }
            // 单机配置
            RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingle();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                configuration.useSingleServer()
                        // 设置redis key前缀
                        .setNameMapper(new KeyPrefixNameMapper(redissonProperties.getKeyPrefix()))
                        .setSubscriptionConnectionMinimumIdleSize(singleServerConfig.getSubscriptionConnectionMinimumIdleSize())
                        .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                        .setSubscriptionConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                        .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize())
                        .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                        .setRetryAttempts(singleServerConfig.getRetryAttempts())
                        .setRetryDelay(new ConstantDelay(Duration.ofMillis(singleServerConfig.getRetryInterval())))
                        // 启用TCP keepAlive保活机制
                        .setKeepAlive(true)
                        // 连接空闲多久开始keep-alive
                        .setTcpKeepAliveIdle(1800)
                        // 两次keep-alive之间的间隔
                        .setTcpKeepAliveInterval(5)
                        // keep-alive多少次之后断开连接
                        .setTcpKeepAliveCount(5)
                        .setTcpNoDelay(true)
                        .setTcpUserTimeout(1_000);
            }
            log.info("初始化 Redisson 配置");
        };
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        FastJson2JsonRedisSerializer<Object> redisSerializer = new FastJson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(redisSerializer);
        template.setHashValueSerializer(redisSerializer);
        return template;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public DistributedLock distributeLock(RedissonClient redisson) {
        return new RedisDistributedLock(redisson);
    }

    static class KeyPrefixNameMapper implements NameMapper {

        private final String keyPrefix;

        public KeyPrefixNameMapper(String keyPrefix) {
            //前缀为空 则返回空前缀
            this.keyPrefix = StringUtils.isBlank(keyPrefix) ? StringUtils.EMPTY : keyPrefix + ":";
        }

        /**
         * 增加前缀
         */
        @Override
        public String map(String name) {
            if (StringUtils.isBlank(name)) {
                return null;
            }
            if (StringUtils.isNotBlank(keyPrefix) && !name.startsWith(keyPrefix)) {
                return keyPrefix + name;
            }
            return name;
        }

        /**
         * 去除前缀
         */
        @Override
        public String unmap(String name) {
            if (StringUtils.isBlank(name)) {
                return null;
            }
            if (StringUtils.isNotBlank(keyPrefix) && name.startsWith(keyPrefix)) {
                return name.substring(keyPrefix.length());
            }
            return name;
        }

    }

    static class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {

        public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        private final Class<T> clazz;

        static {
            ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        }

        public FastJson2JsonRedisSerializer(Class<T> clazz) {
            super();
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONString(t, SerializerFeature.WriteClassName).getBytes(DEFAULT_CHARSET);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length <= 0) {
                return null;
            }
            String str = new String(bytes, DEFAULT_CHARSET);

            return JSON.parseObject(str, clazz);
        }

        public void setObjectMapper(ObjectMapper objectMapper) {
            Assert.notNull(objectMapper, "'objectMapper' must not be null");
        }

        protected JavaType getJavaType(Class<?> clazz) {
            return TypeFactory.defaultInstance().constructType(clazz);
        }
    }

}
