package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.lang.Assert;
import com.xtrarust.cloud.id.core.IdGenerator;
import com.xtrarust.cloud.id.util.SnowflakeIdUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

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
public class Snowflake implements IdGenerator, InitializingBean {

    static {
        EPOCH = LocalDateTime.of(2016, 11, 1, 0, 0, 0)
                .toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()))
                .toEpochMilli();
    }

    // 默认的起始时间
    private static final long EPOCH;

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

    /**
     * 每一部分向左的位移
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS; // 时间毫秒数左移22位
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 数据中心节点左移17位
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 机器节点左移12位

    // 序列掩码，用于限定序列最大值不能超过4095
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 默认最大抖动上限值
    private static final int DEFAULT_VIBRATION_VALUE = 1;

    // 默认时钟回拨容忍度
    private static final int MAX_TOLERATE_TIME_DIFFERENCE_MILLIS = 10;

    // 初始化时间点
    private final long epoch = EPOCH;

    @Getter
    private final long dataCenterId;

    @Getter
    private final long workerId;

    // 最大抖动上限值，注意该值必须小于等于MAX_SEQUENCE即4095
    private final int maxVibrationOffset;

    // 时钟回拨容忍度
    private final int maxTolerateTimeDifferenceMillis;

    //  跨毫秒时的序列号，跨毫秒获取时该序列号+1，达到抖动上限会重置为0
    private final AtomicInteger sequenceOffset = new AtomicInteger(-1);

    private final AtomicLong sequence = new AtomicLong();

    private final AtomicLong lastMillis = new AtomicLong();

    public Snowflake(long dataCenterId, long workerId) {
        this(dataCenterId, workerId, DEFAULT_VIBRATION_VALUE, MAX_TOLERATE_TIME_DIFFERENCE_MILLIS);
    }

    /**
     * @param dataCenterId                    数据中心 id
     * @param workerId                        工作机器节点 id
     * @param maxVibrationOffset              抖动上限值
     * @param maxTolerateTimeDifferenceMillis 时钟回拨容忍度
     */
    public Snowflake(long dataCenterId, long workerId, int maxVibrationOffset, int maxTolerateTimeDifferenceMillis) {
        this.dataCenterId = Assert.checkBetween(dataCenterId, 0, MAX_DATA_CENTER_ID);
        this.workerId = Assert.checkBetween(workerId, 0, MAX_WORKER_ID);
        this.maxVibrationOffset = maxVibrationOffset;
        this.maxTolerateTimeDifferenceMillis = maxTolerateTimeDifferenceMillis;
    }

    @Override
    public synchronized long nextId() {
        long currentMillis = genTime();
        if (waitTolerateTimeDifferenceIfNeed(currentMillis)) {
            currentMillis = genTime();
        }
        if (lastMillis.get() == currentMillis) { // 同一毫秒内
            // 序列号自增，序列号达到最大值4095，等待下一个毫秒再生成
            sequence.set(sequence.incrementAndGet() & SEQUENCE_MASK);
            if (0L == sequence.get()) {
                currentMillis = waitUntilNextTime(currentMillis);
            }
        } else { // 不同毫秒
            // 处理抖动上限，超过了抖动上限则将sequenceOffset计数器归0，否则sequenceOffset累加1
            vibrateSequenceOffset();
            sequence.set(sequenceOffset.get());
        }
        lastMillis.set(currentMillis);
        return ((currentMillis - epoch) << TIMESTAMP_LEFT_SHIFT) // 时间戳部分
                | (dataCenterId << DATA_CENTER_ID_SHIFT) // 数据中心部分
                | (workerId << WORKER_ID_SHIFT) // 机器标识部分
                | sequence.get(); // 序列号部分
    }

    @SneakyThrows(InterruptedException.class)
    private boolean waitTolerateTimeDifferenceIfNeed(final long currentMillis) {
        if (lastMillis.get() <= currentMillis) {
            return false;
        }
        long timeDifferenceMillis = lastMillis.get() - currentMillis;
        if (timeDifferenceMillis >= maxTolerateTimeDifferenceMillis) {
            throw new RuntimeException(String.format("Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds.", lastMillis.get(), currentMillis));
        }
        Thread.sleep(timeDifferenceMillis);
        return true;
    }

    private long waitUntilNextTime(final long lastTime) {
        long result = genTime();
        while (result <= lastTime) {
            result = genTime();
        }
        return result;
    }

    private void vibrateSequenceOffset() {
        if (!sequenceOffset.compareAndSet(maxVibrationOffset, 0)) {
            sequenceOffset.incrementAndGet();
        }
    }

    private long genTime() {
        return System.currentTimeMillis();
    }

    /**
     * 解析雪花 ID
     *
     * @param snowflakeId 雪花 ID
     * @return 雪花 ID 组成部分
     */
    public SnowflakeIdInfo parseSnowflakeId(long snowflakeId) {
        long timestamp = (snowflakeId >> TIMESTAMP_LEFT_SHIFT) + epoch;
        long dataCenterId = (snowflakeId >> DATA_CENTER_ID_SHIFT) & ~(-1L << DATA_CENTER_ID_BITS);
        long workerId = (snowflakeId >> WORKER_ID_SHIFT) & ~(-1L << WORKER_ID_BITS);
        long sequence = snowflakeId & ~(-1L << SEQUENCE_BITS);
        return new SnowflakeIdInfo(timestamp, dataCenterId, workerId, sequence);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        SnowflakeIdUtil.initSnowflake(this);
    }
}
