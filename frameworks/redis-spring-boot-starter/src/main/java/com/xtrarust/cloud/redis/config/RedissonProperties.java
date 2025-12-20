package com.xtrarust.cloud.redis.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Redisson 配置属性
 * 参考<a href="https://github.com/redisson/redisson/wiki/2.-Configuration"></a>
 *
 * @author gova
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {

    /**
     * redis key前缀
     */
    private String keyPrefix = "";

    /**
     * Default value: false
     * <p>Defines whether Redisson connects to Redis only when first Redis call is made and not during Redisson instance creation.
     *
     * <li>true - connects to Redis only when first Redis call is made</li>
     * <li>false - connects to Redis during Redisson instance creation</li>
     */
    private Boolean lazyInitialization = false;

    /**
     * Default value: 16
     * <p>
     * Threads are used to execute listeners logic of RTopic object, invocation handlers of RRemoteService, RTopic object and RExecutorService tasks.
     */
    private int threads = 16;

    /**
     * Default value: 32
     * <p>Threads amount shared between all internal redis clients used by Redisson. Netty threads are used for Redis response decoding and command sending. 0 = cores_amount * 2
     */
    private int nettyThreads = 32;

    /**
     * Default value: TransportMode.NIO
     *
     * <p>Available values:
     *
     * <li>TransportMode.NIO</li>
     * <li>TransportMode.EPOLL - requires netty-transport-native-epoll lib in classpath</li>
     * <li>TransportMode.KQUEUE - requires netty-transport-native-kqueue lib in classpath</li>
     */
    private TransportMode transportMode = TransportMode.NIO;

    /**
     * Default value: RESP2
     *
     * <p>Defines Redis protocol version. Available values: RESP2, RESP3
     */
    private Protocol protocol = Protocol.RESP2;

    /**
     * Default value: 30000
     *
     * <p>RLock object watchdog timeout in milliseconds.
     * This parameter is only used if RLock object acquired without leaseTimeout parameter.
     * Lock expires after lockWatchdogTimeout if watchdog didn't extend it to the next lockWatchdogTimeout time interval.
     * This prevents against infinity locked locks due to Redisson client crash or any other reason when lock can't be released in proper way.
     */
    private long lockWatchdogTimeout = 30000;

    /**
     * Default value: true
     *
     * <p>Defines whether to check synchronized slaves amount with actual slaves amount after lock acquisition.
     */
    private Boolean checkLockSyncedSlaves = true;

    /**
     * Default value: 1000
     *
     * <p>Defines slaves synchronization timeout in milliseconds applied to each operation of RLock, RSemaphore, RPermitExpirableSemaphore objects.
     */
    private long slavesSyncTimeout = 1000;

    /**
     * Default value: 600000
     *
     * <p>Reliable Topic watchdog timeout in milliseconds. Reliable Topic subscriber expires after timeout if watchdog didn't extend it to next timeout time interval. This prevents against infinity grow of stored messages in topic due to Redisson client crush or any other reason when subscriber can't consumer messages anymore.
     */
    private long reliableTopicWatchdogTimeout = 600000;

    /**
     * Default value: false
     *
     * <p>Defines whether to use Lua-script cache on Redis side. Most Redisson methods are Lua-script based and this setting turned on could increase speed of such methods execution and save network traffic.
     */
    private Boolean useScriptCache = false;

    /**
     * Default value: true
     *
     * <p>Defines whether keep PubSub messages handling in arrival order or handle messages concurrently. This setting applied only for PubSub messages per channel.
     */
    private Boolean keepPubSubOrder = true;

    /**
     * Default value: 5
     *
     * <p>Defines minimum delay in seconds for clean up process of expired entries. Applied to JCache, RSetCache, RClusteredSetCache, RMapCache, RListMultimapCache, RSetMultimapCache, RLocalCachedMapCache, RClusteredLocalCachedMapCache objects.
     */
    private int minCleanUpDelay = 5;

    /**
     * Default value: 1800
     *
     * <p>Defines maximum delay in seconds for clean up process of expired entries. Applied to JCache, RSetCache, RClusteredSetCache, RMapCache, RListMultimapCache, RSetMultimapCache, RLocalCachedMapCache, RClusteredLocalCachedMapCache objects.
     */
    private int maxCleanUpDelay = 1800;

    /**
     * Default value: 100
     *
     * <p>Defines expired keys amount deleted per single operation during clean up process of expired entries. Applied to JCache, RSetCache, RClusteredSetCache, RMapCache, RListMultimapCache, RSetMultimapCache, RLocalCachedMapCache, RClusteredLocalCachedMapCache objects.
     */
    private int cleanUpKeysAmount = 100;

    /**
     * 单机服务配置
     */
    private SingleServerConfig single;

    /**
     * 集群服务配置
     */
    private ClusterConfig cluster;

    @Data
    @NoArgsConstructor
    public static class SingleServerConfig {

        /**
         * Default value: 1
         *
         * <p>Minimum idle connection pool size for subscription (pub/sub) channels. Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories.
         */
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * Default value: 50
         *
         * <p>Maximum connection pool size for subscription (pub/sub) channels. Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories.
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Default value: 24
         *
         * <p>Minimum idle Redis connection amount.
         */
        private int connectionMinimumIdleSize = 24;

        /**
         * Default value: 64
         *
         * <p>Redis connection maximum pool size.
         */
        private int connectionPoolSize = 64;

        /**
         * Default value: 10000
         *
         * <p>If pooled connection not used for a timeout time and current connections amount bigger than minimum idle connections pool size, then it will be closed and removed from pool. Value in milliseconds.
         */
        private int idleConnectionTimeout = 10000;

        /**
         * Default value: 10000
         *
         * <p>Timeout in milliseconds during connecting to any Redis server.
         */
        private int connectTimeout = 10000;

        /**
         * Default value: 3000
         *
         * <p>Redis server response timeout in milliseconds. Starts to countdown when Redis command was successfully sent.
         */
        private int timeout = 3000;

        /**
         * Default value: 3
         *
         * <p>Error will be thrown if Redis command can't be sent to Redis server after retryAttempts. But if it sent successfully then timeout will be started.
         */
        private int retryAttempts = 3;

        /**
         * Default value: 1500
         *
         * <p>Time interval in milliseconds after which another one attempt to send Redis command will be executed.
         */
        private int retryInterval = 1500;
    }

    @Data
    @NoArgsConstructor
    public static class ClusterConfig {

        /**
         * Default value: true
         *
         * <p>Enables cluster slots check during Redisson startup.
         */
        private Boolean checkSlotsCoverage = true;

        /**
         * Default value: 1000
         *
         * <p>Scan interval in milliseconds. Applied to Redis clusters topology scan.
         */
        private int scanInterval = 1000;

        /**
         * Default value: SLAVE
         *
         * <p>Set node type used for read operation. Available values:
         *
         * <li>SLAVE - Read from slave nodes, uses MASTER if no SLAVES are available</li>
         * <li>MASTER - Read from master node</li>
         * <li>MASTER_SLAVE - Read from master and slave nodes</li>
         */
        private ReadMode readMode = ReadMode.SLAVE;

        /**
         * Default value: MASTER
         *
         * <p>Set node type used for subscription operation. Available values:
         *
         * <li>SLAVE - Subscribe to slave nodes</li>
         * <li>MASTER - Subscribe to master node</li>
         */
        private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;

        /**
         * Default value: 1
         *
         * <p>Minimum idle connection pool size for subscription (pub/sub) channels. Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories.
         */
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * Default value: 50
         *
         * <p>Maximum connection pool size for subscription (pub/sub) channels. Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories.
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Default value: AUTO
         *
         * <p>Defines whether to use sharded subscription feature available in Redis 7.0+. Used by RMapCache, RLocalCachedMap, RCountDownLatch, RLock, RPermitExpirableSemaphore, RSemaphore, RLongAdder, RDoubleAdder, Micronaut Session, Apache Tomcat Manager objects.
         */
        private ShardedSubscriptionMode shardedSubscriptionMode = ShardedSubscriptionMode.AUTO;

        /**
         * Default value: 24
         *
         * <p>Redis 'slave' node minimum idle connection amount for each slave node.
         */
        private int slaveConnectionMinimumIdleSize = 24;

        /**
         * Default value: 64
         *
         * <p>Redis 'slave' node maximum connection pool size for each slave node
         */
        private int slaveConnectionPoolSize = 64;

        /**
         * Default value: 24
         *
         * <p>Minimum idle connections amount per Redis master node.
         */
        private int masterConnectionMinimumIdleSize = 24;

        /**
         * Default value: 24
         *
         * <p>Redis 'master' node maximum connection pool size
         */
        private int masterConnectionPoolSize = 24;

        /**
         * Default value: 10000
         *
         * <p>If pooled connection not used for a timeout time and current connections amount bigger than minimum idle connections pool size, then it will be closed and removed from pool. Value in milliseconds.
         */
        private int idleConnectionTimeout = 10000;

        /**
         * Default value: 10000
         *
         * <p>Timeout in milliseconds during connecting to any Redis server.
         */
        private int connectTimeout = 10000;

        /**
         * Default value: 3000
         *
         * <p>Redis server response timeout in milliseconds. Starts to countdown when Redis command was successfully sent.
         */
        private int timeout = 3000;

        /**
         * Default value: 3
         *
         * <p>Error will be thrown if Redis command can't be sent to Redis server after retryAttempts. But if it sent successfully then timeout will be started.
         */
        private int retryAttempts = 3;

        /**
         * Default value: 1500
         *
         * <p>Time interval in milliseconds after which another one attempt to send Redis command will be executed.
         */
        private int retryInterval = 1500;

        private String password;
    }

}
