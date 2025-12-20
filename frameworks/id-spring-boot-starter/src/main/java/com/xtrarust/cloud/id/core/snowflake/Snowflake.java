package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.xtrarust.cloud.id.core.IdGenerator;

import java.util.Calendar;

/**
 * Twitter的Snowflake 算法<br>
 * 分布式系统中，有一些需要使用全局唯一ID的场景，有些时候我们希望能使用一种简单一些的ID，并且希望ID能够按照时间有序生成。
 *
 * <p>
 * snowflake的结构如下(每部分用-分开):<br>
 *
 * <pre>
 * 符号位（1bit）- 时间戳相对值（41bit）- 数据中心标志（5bit）- 机器标志（5bit）- 递增序号（12bit）
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * </pre>
 * <p>
 * 第一位未使用(符号位表示正数)，接下来的41位为毫秒级时间(41位的长度可以使用69年)<br>
 * 然后是5位datacenterId和5位workerId(10位的长度最多支持部署 32* 32 = 1024 个节点）<br>
 * 最后12位是毫秒内的计数（12位的计数顺序号支持每个节点每毫秒产生4096个ID序号）
 * <p>
 * 并且可以通过生成的id反推出生成时间,datacenterId和workerId
 * <p>
 * 参考：<br>
 * <a href="http://www.cnblogs.com/relucent/p/4955340.html">Twitter的分布式自增ID算法snowflake (Java版)</a><br>
 * <a href="https://blog.csdn.net/u012988901/article/details/131720235">雪花算法生成分布式ID源码分析及低频场景下全是偶数的解决办法</a><br>
 * 1. 切换毫秒时使用随机数（hutool实现）
 * 2. 抖动上限值加抖动序列号（Sharding JDBC实现）
 *
 * @author gova
 */
public class Snowflake implements IdGenerator {

    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.NOVEMBER, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        DEFAULT_EPOCH = calendar.getTimeInMillis();
    }

    // 默认的起始时间
    private static final long DEFAULT_EPOCH;

    // 默认回拨时间：2S
    private static final long DEFAULT_TIME_OFFSET = 2000L;

    // 默认最大抖动上限值
    private static final int DEFAULT_VIBRATION_VALUE = 1;

    /**
     * 每一部分占用的位数
     */
    private static final long DATA_CENTER_ID_BITS = 5L; // 数据中心5位
    private static final long WORKER_ID_BITS = 5L; // 机器标识5位
    private static final long SEQUENCE_BITS = 12L; // 序列号12位

    /**
     * 每一部分的最大值
     */
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    public static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS); // 最大支持数据中心节点数 31
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS); // 最大支持机器节点数 31
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    public final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS); // 最大序列号 4095

    /**
     * 每一部分向左的位移
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS; // 时间毫秒数左移22位
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 数据中心节点左移17位
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 机器节点左移12位

    // 序列掩码，用于限定序列最大值不能超过4095
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 初始化时间点
    private final long epoch = DEFAULT_EPOCH;

    private final long dataCenterId;

    private final long workerId;

    private final boolean useSystemClock;

    // 允许的时钟回拨毫秒数
    private final long timeOffset;

    private volatile long sequence = 0L;

    private volatile long lastTimestamp = -1L;

    // 限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题，0表示无随机，上限不包括值本身
    // private final long randomSequenceLimit;

    // 最大抖动上限值，最好设置为奇数，注意该值必须小于等于MAX_SEQUENCE即4095
    private final int maxVibrationOffset;

    // 跨毫秒时的序列号，跨毫秒获取时该序列号+1，达到抖动上限会重置为0
    private volatile int sequenceOffset = -1;

    public Snowflake() {
        this(IdUtil.getWorkerId(IdUtil.getDataCenterId(MAX_DATA_CENTER_ID), MAX_WORKER_ID));
    }

    public Snowflake(long workerId) {
        this(IdUtil.getDataCenterId(MAX_DATA_CENTER_ID), workerId);
    }

    public Snowflake(long dataCenterId, long workerId) {
        this(dataCenterId, workerId, false);
    }

    public Snowflake(long dataCenterId, long workerId, boolean isUseSystemClock) {
        this(dataCenterId, workerId, isUseSystemClock, DEFAULT_TIME_OFFSET);
    }

    public Snowflake(long dataCenterId, long workerId, boolean isUseSystemClock, long timeOffset) {
        this(dataCenterId, workerId, isUseSystemClock, timeOffset, DEFAULT_VIBRATION_VALUE);
    }

    /**
     * @param dataCenterId       数据中心id
     * @param workerId           工作机器节点id
     * @param isUseSystemClock   是否使用{@link SystemClock} 获取当前时间戳
     * @param timeOffset         允许时间回拨的毫秒数
     * @param maxVibrationOffset 抖动上限值
     */
    public Snowflake(long dataCenterId, long workerId, boolean isUseSystemClock, long timeOffset, int maxVibrationOffset) {
        this.dataCenterId = Assert.checkBetween(dataCenterId, 0, MAX_DATA_CENTER_ID);
        this.workerId = Assert.checkBetween(workerId, 0, MAX_WORKER_ID);
        this.useSystemClock = isUseSystemClock;
        this.timeOffset = timeOffset;
        this.maxVibrationOffset = maxVibrationOffset;
    }

    /**
     * 根据Snowflake的ID，获取生成时间
     *
     * @param id snowflake算法生成的id
     * @return 生成的时间
     */
    public long getGenerateDateTime(long id) {
        return (id >> TIMESTAMP_LEFT_SHIFT & ~(-1L << 41L)) + epoch;
    }

    /**
     * 根据Snowflake的ID，获取机器id
     *
     * @param id snowflake算法生成的id
     * @return 所属机器的id
     */
    public long getWorkerId(long id) {
        return id >> WORKER_ID_SHIFT & ~(-1L << WORKER_ID_BITS);
    }

    /**
     * 根据Snowflake的ID，获取数据中心id
     *
     * @param id snowflake算法生成的id
     * @return 所属数据中心
     */
    public long getDataCenterId(long id) {
        return id >> DATA_CENTER_ID_SHIFT & ~(-1L << DATA_CENTER_ID_BITS);
    }

    /**
     * 下一个ID
     *
     * @return ID
     */
    @SuppressWarnings("all")
    public synchronized long nextId() {
        long timestamp = genTime();
        if (timestamp < lastTimestamp) {
            if (lastTimestamp - timestamp < timeOffset) {
                // 容忍指定的回拨，避免NTP校时造成的异常
                timestamp = lastTimestamp;
            } else {
                // 如果服务器时间有问题(时钟后退) 报错
                throw new IllegalStateException(StrUtil.format("Clock moved backwards. Refusing to generate id for {}ms", lastTimestamp - timestamp));
            }
        }
        if (timestamp == lastTimestamp) {
            // 1. 同一毫秒内序列号自增，序列号达到最大值4095，等待下一个毫秒再生成
            if (0L == (sequence = (sequence + 1) & SEQUENCE_MASK)) {
                timestamp = waitUntilNextTime(lastTimestamp);
            }
        } else {
            // 2. 不同毫秒，序列号重置为0
            // 为避免低频模式下都是偶数的情况，序列号取一个[0,randomSequenceLimit)之间的随机数）
            // sequence = randomSequenceLimit > 1 ? ThreadLocalRandom.current().nextLong(randomSequenceLimit) : 0L;
            // 处理抖动上限，超过了抖动上限则将sequenceOffset计数器归0，否则sequenceOffset累加1
            vibrateSequenceOffset();
            sequence = sequenceOffset;
        }
        // 3. 当前时间戳存档，用于下次生成时对比是否是同一毫秒内
        lastTimestamp = timestamp;
        // 4. 或运算拼接id并返回
        return ((timestamp - epoch) << TIMESTAMP_LEFT_SHIFT) // 时间戳部分
                | (dataCenterId << DATA_CENTER_ID_SHIFT) // 数据中心部分
                | (workerId << WORKER_ID_SHIFT) // 机器标识部分
                | sequence; // 序列号部分
    }

    private long waitUntilNextTime(long lastTimestamp) {
        long timestamp = genTime();
        while (timestamp <= lastTimestamp) {
            timestamp = genTime();
        }
        return timestamp;
    }

    private long genTime() {
        return useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    private void vibrateSequenceOffset() {
        sequenceOffset = sequenceOffset >= maxVibrationOffset ? 0 : sequenceOffset + 1;
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     *
     * @param snowflakeId 雪花算法 ID
     * @return 反解析雪花ID对象
     */
    public SnowflakeIdInfo parseSnowflakeId(long snowflakeId) {
        return SnowflakeIdInfo.builder()
                .timestamp((snowflakeId >> TIMESTAMP_LEFT_SHIFT) + epoch)
                .dataCenterId((int) ((snowflakeId >> DATA_CENTER_ID_SHIFT) & ~(-1L << DATA_CENTER_ID_BITS)))
                .workerId((int) ((snowflakeId >> WORKER_ID_SHIFT) & ~(-1L << WORKER_ID_BITS)))
                .sequence((int) (snowflakeId & ~(-1L << SEQUENCE_BITS)))
                .build();
    }
}
