package com.xtrarust.cloud.redis.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.redisson.config.*;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Redisson 配置属性
 * 参考<a href="https://redisson.pro/docs/configuration/">Redisson配置</a>
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
     * <p>Defines whether Redisson connects to Redis only when the first Redis call is made or if Redisson connects during creation of the Redisson instance.</p>
     *
     * <li>true - connects to Redis only when the first Redis call is made</li>
     * <li>false - connects to Redis upon Redisson instance creation</li>
     */
    private boolean lazyInitialization = false;

    /**
     * Default value: 16
     * <p>Threads are used to execute the listener's logic of the RTopic object, invocation handlers of the RRemoteService, the RTopic object and RExecutorService tasks.</p>
     */
    private int threads = 16;

    /**
     * Default value: 32
     * <p>Defines the number of threads shared between all internal Redis clients used by Redisson. Netty threads are used for Redis response decoding and command sending. 0 = cores_amount * 2</p>
     */
    private int nettyThreads = 32;

    /**
     * Default value: TransportMode.NIO
     *
     * <p>Available values:</p>
     *
     * <li>TransportMode.NIO</li>
     * <li>TransportMode.EPOLL - requires netty-transport-native-epoll lib in classpath</li>
     * <li>TransportMode.KQUEUE - requires netty-transport-native-kqueue lib in classpath</li>
     */
    private TransportMode transportMode = TransportMode.NIO;

    /**
     * Default value: RESP2
     *
     * <p>Defines the Redis protocol version. Available values: RESP2, RESP3</p>
     */
    private Protocol protocol = Protocol.RESP2;

    /**
     * Default value: 30000
     *
     * <p>
     * RLock object watchdog timeout in milliseconds.
     * This parameter is only used if an RLock object is acquired without the leaseTimeout parameter.
     * The lock expires after lockWatchdogTimeout if the watchdog didn’t extend it to the next lockWatchdogTimeout time interval.
     * This prevents infinity-locked locks due to a Redisson client crash, or any other reason why a lock can’t be released properly.
     * </p>
     */
    private long lockWatchdogTimeout = 30000;

    /**
     * Default value: 100
     *
     * <p>Amount of locks used by a single lock watchdog execution. This parameter is only used if lock has been acquired without leaseTimeout parameter definition. </p>
     */
    private int lockWatchdogBatchSize = 100;

    /**
     * Default value: true
     *
     * <p>Defines whether to check the synchronized slaves amount with the actual slaves amount after lock acquisition.</p>
     */
    private boolean checkLockSyncedSlaves = true;

    /**
     * Default value: 1000
     *
     * <p>Defines the slaves synchronization timeout in milliseconds, applied to each operation of the RLock, RSemaphore, RPermitExpirableSemaphore objects.</p>
     */
    private long slavesSyncTimeout = 1000;

    /**
     * Default value: 600000
     *
     * <p>
     * Reliable Topic watchdog timeout in milliseconds.
     * Reliable Topic subscriber expires after timeout if the watchdog didn’t extend it to the next timeout time interval.
     * This prevents the infinite growing of stored messages in a topic, due to a Redisson client crush or any other reason when a subscriber can’t consume messages anymore.
     * </p>
     */
    private long reliableTopicWatchdogTimeout = 600000;

    /**
     * Default value: true
     *
     * <p>
     * Defines whether to use the Lua-script cache on the Redis side.
     * Most Redisson methods are Lua-script-based, and turning this setting on could increase the speed of such methods' execution and save network traffic.
     * </p>
     */
    private boolean useScriptCache = true;

    /**
     * Default value: true
     *
     * <p>
     * Defines whether to keep PubSub messages handling in arrival order, or to handle messages concurrently.
     * This setting is applied only for PubSub messages per channel.
     * </p>
     */
    private boolean keepPubSubOrder = true;

    /**
     * Default value: 5
     *
     * <p>
     * Defines the minimum delay in seconds for the cleanup process of expired entries.
     * Applied to JCache, RSetCache, RClusteredSetCache, RMapCache, RListMultimapCache, RSetMultimapCache, RLocalCachedMapCache, RClusteredLocalCachedMapCache objects.
     * </p>
     */
    private int minCleanUpDelay = 5;

    /**
     * Default value: 1800
     *
     * <p>
     * Defines maximum delay in seconds for clean up process of expired entries.
     * Applied to JCache, RSetCache, RClusteredSetCache, RMapCache, RListMultimapCache, RSetMultimapCache, RLocalCachedMapCache, RClusteredLocalCachedMapCache objects.
     * </p>
     */
    private int maxCleanUpDelay = 1800;

    /**
     * Default value: 100
     *
     * <p>
     * Defines the amount of expired keys deleted per single operation during the cleanup process of expired entries.
     * Applied to JCache, RSetCache, RClusteredSetCache, RMapCache, RListMultimapCache, RSetMultimapCache, RLocalCachedMapCache, RClusteredLocalCachedMapCache objects.
     * </p>
     */
    private int cleanUpKeysAmount = 100;

    /**
     * 单机配置
     */
    private SingleServerConfig single;

    /**
     * 哨兵配置
     */
    private SentinelConfig sentinel;

    /**
     * 集群配置
     */
    private ClusterConfig cluster;

    /**
     * 注：username、password、connectTimeout、timeout与clientName通过Spring Data Redis属性进行配置
     *
     * @see org.redisson.config.BaseConfig
     * @see RedisProperties#getUsername()
     * @see RedisProperties#getPassword()
     * @see RedisProperties#getConnectTimeout()
     * @see RedisProperties#getTimeout()
     * @see RedisProperties#getClientName()
     */
    @Data
    @NoArgsConstructor
    static class BaseConfig {

        /**
         * Default value: 10000
         *
         * <p>
         * If a pooled connection is not used for a timeout time and current connections amount bigger than minimum idle connections pool size,
         * then it will be closed and removed from the pool. Value in milliseconds.
         * </p>
         */
        private int idleConnectionTimeout = 10_000;

        /**
         * Default value: 7500
         *
         * <p>Defines subscription timeout in milliseconds, applied per channel subscription.</p>
         */
        private int subscriptionTimeout = 7500;

        /**
         * Default value: 4
         *
         * <p>
         * Error will be thrown if Redis command can’t be sent to Redis server after retryAttempts.
         * But if it was sent successfully then timeout will be started.
         * </p>
         */
        private int retryAttempts = 4;

        // 这里默认使用 EqualJitterDelay(Duration baseDelay, Duration maxDelay)
        private Duration retryBaseDelay = Duration.ofMillis(1000);
        private Duration retryMaxDelay = Duration.ofSeconds(2);

        private Duration reconnectionBaseDelay = Duration.ofMillis(100);
        private Duration reconnectionMaxDelay = Duration.ofSeconds(10);

        /**
         * Default value: 5
         *
         * <p>
         * Subscriptions per subscribe connection limit.
         * Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories.
         * </p>
         */
        private int subscriptionsPerConnection = 5;

        /**
         * Default value: 30000
         *
         * <p></p>
         */
        private int pingConnectionInterval = 30000;

        /**
         * Default value: false
         *
         * <p>Enables TCP keepAlive for connections.</p>
         */
        private boolean keepAlive = false;

        /**
         * Default value: 0
         *
         * <p>
         * This defines the maximum number of keepalive probes TCP should send before dropping the connection.
         * A 0 value means to use the system's default setting.
         * </p>
         */
        private int tcpKeepAliveCount = 0;

        /**
         * Default value: 0
         *
         * <p>
         * Defines the time in seconds the connection needs to remain idle before TCP starts sending keepalive probes.
         * A 0 value means use the system's default setting
         * </p>
         */
        private int tcpKeepAliveIdle = 0;

        /**
         * Default value: 0
         *
         * <p>
         * Defines the time in seconds between individual keepalive probes.
         * 0 value means use the system's default setting.
         * </p>
         */
        private int tcpKeepAliveInterval = 0;

        /**
         * Default value: 0
         *
         * <p>
         * Defines the maximum amount of time in milliseconds that transmitted data may remain unacknowledged or buffered data may remain untransmitted (due to zero window size) before TCP will forcibly close the connection.
         * A 0 value means use the system's default setting.
         * </p>
         */
        private int tcpUserTimeout = 0;

        /**
         * Default value: true
         *
         * <p>Enables TCP noDelay for connections.</p>
         */
        private boolean tcpNoDelay = true;
    }

    /**
     * 注：address、database通过Spring Data Redis属性进行配置
     *
     * @see org.redisson.config.SingleServerConfig
     * @see RedisProperties#getHost()
     * @see RedisProperties#getPort()
     * @see RedisProperties#getDatabase()
     */
    @Data
    @NoArgsConstructor
    public static class SingleServerConfig extends BaseConfig {

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
    }

    /**
     * @see org.redisson.config.BaseMasterSlaveServersConfig
     */
    @Data
    @NoArgsConstructor
    static class BaseMasterSlaveConfig extends BaseConfig {

        /**
         * Default value: 24
         *
         * <p>Redis slave node minimum idle connection amount for each slave node.</p>
         */
        private int slaveConnectionMinimumIdleSize = 24;

        /**
         * Default value: 64
         *
         * <p>Redis slave node maximum connection pool size for each slave node.</p>
         */
        private int slaveConnectionPoolSize = 64;

        /**
         * Default value: 3000
         *
         * <p>
         * The interval of Redis Slave reconnection attempts when excluded from the internal list of available servers.
         * On each timeout event, Redisson tries to connect to a disconnected Redis server. Value in milliseconds.
         * </p>
         */
        private int failedSlaveReconnectionInterval = 3000;

        /**
         * Default value: 24
         *
         * <p>Minimum idle connections amount per Redis master node.</p>
         */
        private int masterConnectionMinimumIdleSize = 24;

        /**
         * Default value: 64
         *
         * <p>Redis master node maximum connection pool size.</p>
         */
        private int masterConnectionPoolSize = 64;

        /**
         * Default value: SLAVE
         *
         * <p>Set node type used for read operation. Available values: </p>
         * <li>SLAVE - Read from slave nodes, uses MASTER if no SLAVES are available</li>
         * <li>MASTER - Read from master node</li>
         * <li>MASTER_SLAVE - Read from master and slave nodes</li>
         */
        private ReadMode readMode = ReadMode.SLAVE;

        /**
         * Default value: MASTER
         *
         * <p>Set node type used for subscription operation. Available values: </p>
         * <li>SLAVE - Subscribe to slave nodes</li>
         * <li>MASTER - Subscribe to master node</li>
         */
        private SubscriptionMode subscriptionMode = SubscriptionMode.MASTER;

        /**
         * Default value: 1
         *
         * <p>
         * Minimum idle connection pool size for subscription (pub/sub) channels.
         * Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories.
         * </p>
         */
        private int subscriptionConnectionMinimumIdleSize = 1;

        /**
         * Default value: 50
         *
         * <p>
         * Maximum connection pool size for subscription (pub/sub) channels.
         * Used by RTopic, RPatternTopic, RLock, RSemaphore, RCountDownLatch, RClusteredLocalCachedMap, RClusteredLocalCachedMapCache, RLocalCachedMap, RLocalCachedMapCache objects and Hibernate Local Cached Region Factories
         * </p>
         */
        private int subscriptionConnectionPoolSize = 50;

        /**
         * Default value: 5000
         *
         * <p>Interval in milliseconds to check the endpoint's DNS. Set -1 to disable.</p>
         */
        private long dnsMonitoringInterval = 5000;
    }

    /**
     * 注：masterName、sentinelUsername、sentinelPassword、database通过Spring Data Redis属性进行配置
     *
     * @see org.redisson.config.SentinelServersConfig
     * @see RedisProperties#getSentinel()
     * @see RedisProperties#getDatabase()
     */
    @Data
    @NoArgsConstructor
    public static class SentinelConfig extends BaseMasterSlaveConfig {

        private int scanInterval = 1000;

        /**
         * Default value: true
         *
         * <p>Enables sentinels list check during Redisson startup.</p>
         */
        private boolean checkSentinelsList = true;

        /**
         * Default value: true
         *
         * <p>Check if slave node master-link-status field has status ok.</p>
         */
        private boolean checkSlaveStatusWithSyncing = true;

        /**
         * Default value: true
         *
         * <p>Enables sentinels discovery.</p>
         */
        private boolean sentinelsDiscovery = true;
    }

    /**
     * 注：nodeAddresses通过Spring Data Redis属性进行配置
     *
     * @see org.redisson.config.ClusterServersConfig
     * @see RedisProperties#getCluster()
     */
    @Data
    @NoArgsConstructor
    public static class ClusterConfig extends BaseMasterSlaveConfig {

        /**
         * Default value: 1000
         *
         * <p>Scan interval in milliseconds. Applied to Redis clusters topology scans.</p>
         */
        private int scanInterval = 1000;

        /**
         * Default value: true
         *
         * <p>Enables cluster slots check during Redisson startup.</p>
         */
        private boolean checkSlotsCoverage = true;

        /**
         * Default value: AUTO
         *
         * <p>
         * Defines whether to use sharded subscription feature available in Redis 7.0 and higher.
         * Used by RMapCache, RLocalCachedMap, RCountDownLatch, RLock, RPermitExpirableSemaphore, RSemaphore, RLongAdder, RDoubleAdder, Micronaut Session, Apache Tomcat Manager objects.
         * </p>
         */
        private ShardedSubscriptionMode shardedSubscriptionMode = ShardedSubscriptionMode.AUTO;
    }

}
