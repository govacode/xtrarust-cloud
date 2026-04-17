package com.xtrarust.cloud.id.core.gene;

import cn.hutool.core.lang.Assert;
import com.xtrarust.cloud.id.core.GeneIdGenerator;
import com.xtrarust.cloud.id.core.snowflake.AbstractSnowflake;
import lombok.Getter;
import lombok.SneakyThrows;

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
public final class GeneSnowflake extends AbstractSnowflake implements GeneIdGenerator {

    // 序列掩码
    private final long SEQUENCE_MASK;

    // 基因位数
    private final long geneBits;

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
        super(dataCenterId, workerId, maxVibrationOffset, maxTolerateTimeDifferenceMillis);
        this.geneBits = Assert.checkBetween(geneBits, 0, SEQUENCE_BITS);
        this.SEQUENCE_MASK = (1L << SEQUENCE_BITS - geneBits) - 1;
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

    public GeneIdInfo parseSnowflakeId(long snowflakeId) {
        long timestamp = (snowflakeId >> TIMESTAMP_LEFT_SHIFT) + epoch;
        long dataCenterId = (snowflakeId >> DATA_CENTER_ID_SHIFT) & ~(-1L << DATA_CENTER_ID_BITS);
        long workerId = (snowflakeId >> WORKER_ID_SHIFT) & ~(-1L << WORKER_ID_BITS);
        long sequence = (snowflakeId >> geneBits) & ~(-1L << (SEQUENCE_BITS - geneBits));
        long gene = snowflakeId & ~(-1L << geneBits);
        return new GeneIdInfo(timestamp, dataCenterId, workerId, sequence, gene);
    }

}
