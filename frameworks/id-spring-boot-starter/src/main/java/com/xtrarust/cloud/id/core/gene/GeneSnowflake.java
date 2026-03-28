package com.xtrarust.cloud.id.core.gene;

import cn.hutool.core.lang.Assert;
import com.xtrarust.cloud.id.core.GeneIdGenerator;
import lombok.Getter;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 基因法雪花 ID 生成器<br>
 * 原理：<br>
 * 一个数对2的n次方取余，那么余数就是这个数的二进制的最后n位数，同HashMap取数组下标算法 (2^n - 1) & hash === hash % 2^n<br>
 * 因此如果需要让本ID生成器与某个雪花ID对2^n取模运算结果相同只需让最后n位与雪花ID最后n位保持一致即可<br>
 * <p>
 * 注意：由于基因和序列号共占12位，当基因位占用位数较多时每毫秒生成序列号减少（如基因位6位时每毫秒可生成2^6-1=63个序列号），导致发号器性能下降<br>
 * 相比原始雪花算法每秒可生成 4096000 个id（百万级），基因位6位时每秒只能生成 63000 个id（万级）
 *
 * @author gova
 */
public final class GeneSnowflake implements GeneIdGenerator {

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

    private final long geneBits;

    // 最大抖动上限值，注意该值必须小于等于MAX_SEQUENCE即4095
    private final int maxVibrationOffset;

    // 时钟回拨容忍度
    private final int maxTolerateTimeDifferenceMillis;

    //  跨毫秒时的序列号，跨毫秒获取时该序列号+1，达到抖动上限会重置为0
    private final AtomicInteger sequenceOffset = new AtomicInteger(-1);

    private final AtomicLong sequence = new AtomicLong();

    private final AtomicLong lastMillis = new AtomicLong();

    public GeneSnowflake(long dataCenterId, long workerId, long geneBits) {
        this(dataCenterId, workerId, geneBits, DEFAULT_VIBRATION_VALUE, MAX_TOLERATE_TIME_DIFFERENCE_MILLIS);
    }

    /**
     * @param dataCenterId                    数据中心 id
     * @param workerId                        工作机器节点 id
     * @param geneBits                        基因位数
     * @param maxVibrationOffset              抖动上限
     * @param maxTolerateTimeDifferenceMillis 时钟回拨容忍度
     */
    public GeneSnowflake(long dataCenterId, long workerId, long geneBits, int maxVibrationOffset, int maxTolerateTimeDifferenceMillis) {
        this.dataCenterId = Assert.checkBetween(dataCenterId, 0, MAX_DATA_CENTER_ID);
        this.workerId = Assert.checkBetween(workerId, 0, MAX_WORKER_ID);
        this.geneBits = Assert.checkBetween(geneBits, 0, SEQUENCE_GENE_BITS);
        this.SEQUENCE_MASK = (1L << SEQUENCE_GENE_BITS - geneBits) - 1;
        this.maxVibrationOffset = maxVibrationOffset;
        this.maxTolerateTimeDifferenceMillis = maxTolerateTimeDifferenceMillis;
    }

    @Override
    public synchronized long nextId(long serviceId) {
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
                | (sequence.get() << geneBits) // 序列号部分
                // 基因部分（取serviceId决定取模结果的后n位）
                | serviceId & ((1L << geneBits) - 1);
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

    public GeneIdInfo parseSnowflakeId(long snowflakeId) {
        long timestamp = (snowflakeId >> TIMESTAMP_LEFT_SHIFT) + epoch;
        long dataCenterId = (snowflakeId >> DATA_CENTER_ID_SHIFT) & ~(-1L << DATA_CENTER_ID_BITS);
        long workerId = (snowflakeId >> WORKER_ID_SHIFT) & ~(-1L << WORKER_ID_BITS);
        long sequence = (snowflakeId >> geneBits) & ~(-1L << (SEQUENCE_GENE_BITS - geneBits));
        long gene = snowflakeId & ~(-1L << geneBits);
        return new GeneIdInfo(timestamp, dataCenterId, workerId, sequence, gene);
    }

}
