package com.xtrarust.cloud.id.core.gene;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.xtrarust.cloud.id.core.GeneIdGenerator;
import com.xtrarust.cloud.id.core.snowflake.SnowflakeIdInfo;

import java.util.Calendar;
import java.util.Date;

/**
 * 基因法雪花ID生成器<br>
 * 原理：<br>
 * 一个数对2的n次方取余，那么余数就是这个数的二进制的最后n位数，同HashMap取数组下标算法 (2^n - 1) & hash === hash % 2^n<br>
 * 因此如果需要让本ID生成器与某个雪花ID对2^n取模运算结果相同只需让最后n位与雪花ID最后n位保持一致即可<br>
 *
 * 注意：由于基因和序列号共占12位，当基因位占用位数较多时每毫秒生成序列号减少（如基因位6位时每毫秒可生成2^6-1=63个序列号），导致发号器性能下降<br>
 * 相比原始雪花算法每秒可生成 4096000 个id（百万级），基因位6位时每秒只能生成 63000 个id（万级）
 *
 * @author gova
 */
public final class SnowflakeGeneIdGenerator implements GeneIdGenerator {

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
    private static final long SEQUENCE_GENE_BITS = 12L; // 序列号 + 基因共12位

    /**
     * 每一部分的最大值
     */
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    public static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS); // 最大支持数据中心节点数 31
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    private static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS); // 最大支持机器节点数 31

    /**
     * 每一部分向左的位移
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_GENE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS; // 时间毫秒数左移22位
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_GENE_BITS + WORKER_ID_BITS; // 数据中心节点左移17位
    private static final long WORKER_ID_SHIFT = SEQUENCE_GENE_BITS; // 机器节点左移12位

    // 序列掩码
    private final long SEQUENCE_MASK;

    // 初始化时间点
    private final long epoch = DEFAULT_EPOCH;

    private final long dataCenterId;

    private final long workerId;

    // 基因位数（与序列号共占12位，如基因位数为5则序列号占7位，每毫秒最多生成127个序列号）
    private final long geneBits;

    private final boolean useSystemClock;

    // 允许的时钟回拨毫秒数
    private final long timeOffset;

    private volatile long sequence = 0L;

    private volatile long lastTimestamp = -1L;

    // 限定一个随机上限，在不同毫秒下生成序号时，给定一个随机数，避免偶数问题，0表示无随机，上限不包括值本身
    // private final long randomSequenceLimit;

    // 最大抖动上限值，最好设置为奇数
    private final int maxVibrationOffset;

    // 跨毫秒时的序列号，跨毫秒获取时该序列号+1，达到抖动上限会重置为0
    private volatile int sequenceOffset = -1;

    public SnowflakeGeneIdGenerator(long geneBits) {
        this(IdUtil.getWorkerId(IdUtil.getDataCenterId(MAX_DATA_CENTER_ID), MAX_WORKER_ID), geneBits);
    }

    public SnowflakeGeneIdGenerator(long workerId, long geneBits) {
        this(IdUtil.getDataCenterId(MAX_DATA_CENTER_ID), workerId, geneBits);
    }

    public SnowflakeGeneIdGenerator(long dataCenterId, long workerId, long geneBits) {
        this(dataCenterId, workerId, geneBits, false);
    }

    public SnowflakeGeneIdGenerator(long dataCenterId, long workerId, long geneBits, boolean isUseSystemClock) {
        this(null, dataCenterId, workerId, geneBits, isUseSystemClock);
    }

    public SnowflakeGeneIdGenerator(Date epochDate, long dataCenterId, long workerId, long geneBits, boolean isUseSystemClock) {
        this(epochDate, dataCenterId, workerId, geneBits, isUseSystemClock, DEFAULT_TIME_OFFSET);
    }

    public SnowflakeGeneIdGenerator(Date epochDate, long dataCenterId, long workerId, long geneBits, boolean isUseSystemClock, long timeOffset) {
        this(epochDate, dataCenterId, workerId, geneBits, isUseSystemClock, timeOffset, DEFAULT_VIBRATION_VALUE);
    }

    /**
     * @param epochDate           初始化时间起点（null表示默认起始日期）,后期修改会导致id重复,如果要修改连workerId dataCenterId，慎用
     * @param dataCenterId        数据中心id
     * @param workerId            工作机器节点id
     * @param geneBits            基因位数
     * @param isUseSystemClock    是否使用{@link SystemClock} 获取当前时间戳
     * @param timeOffset          允许时间回拨的毫秒数
     * @param maxVibrationOffset  抖动上限值
     */
    public SnowflakeGeneIdGenerator(Date epochDate, long dataCenterId, long workerId, long geneBits, boolean isUseSystemClock, long timeOffset, int maxVibrationOffset) {
        this.dataCenterId = Assert.checkBetween(dataCenterId, 0, MAX_DATA_CENTER_ID);
        this.workerId = Assert.checkBetween(workerId, 0, MAX_WORKER_ID);
        this.geneBits = Assert.checkBetween(geneBits, 0, SEQUENCE_GENE_BITS);
//        this.SEQUENCE_MASK = ~(-1L << (SEQUENCE_GENE_BITS));
        this.SEQUENCE_MASK = (1L << SEQUENCE_GENE_BITS - geneBits) - 1;
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
    public synchronized long nextId(long serviceId) {
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
            // 1. 同一毫秒内序列号自增，序列号达到最大值，等待下一个毫秒再生成
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
                | (sequence << geneBits) // 序列号部分
                // 基因部分（取serviceId决定取模结果的后n位）
                | serviceId & ((1L << geneBits) - 1);
    }

    private long waitUntilNextTime(long lastTimestamp) {
        long timestamp = genTime();
        while (timestamp <= lastTimestamp) {
            timestamp = genTime();
        }
        return timestamp;
    }

    /**
     * 生成时间戳
     *
     * @return 时间戳
     */
    private long genTime() {
        return this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
    }

    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    private void vibrateSequenceOffset() {
        sequenceOffset = sequenceOffset >= maxVibrationOffset ? 0 : sequenceOffset + 1;
    }

    public SnowflakeIdInfo parseSnowflakeId(long snowflakeId) {
        return SnowflakeIdInfo.builder()
                .timestamp((snowflakeId >> TIMESTAMP_LEFT_SHIFT) + epoch)
                .dataCenterId((int) ((snowflakeId >> DATA_CENTER_ID_SHIFT) & ~(-1L << DATA_CENTER_ID_BITS)))
                .workerId((int) ((snowflakeId >> WORKER_ID_SHIFT) & ~(-1L << WORKER_ID_BITS)))
                .sequence((int) ((snowflakeId >> geneBits) & ~(-1L << (SEQUENCE_GENE_BITS - geneBits))))
                .gene((int) (snowflakeId & ~(-1L << geneBits)))
                .build();
    }

}
