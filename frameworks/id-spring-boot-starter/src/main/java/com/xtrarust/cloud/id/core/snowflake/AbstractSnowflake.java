package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import lombok.SneakyThrows;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractSnowflake {

    static {
        EPOCH = LocalDateTime.of(2016, 11, 1, 0, 0, 0)
                .toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now()))
                .toEpochMilli();
    }

    // 默认的起始时间
    protected static final long EPOCH;

    /**
     * 每一部分占用的位数
     */
    protected static final long DATA_CENTER_ID_BITS = 5L; // 数据中心5位
    protected static final long WORKER_ID_BITS = 5L; // 机器标识5位
    protected static final long SEQUENCE_BITS = 12L; // 序列号12位

    /**
     * 每一部分的最大值
     */
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    protected static final long MAX_DATA_CENTER_ID = -1L ^ (-1L << DATA_CENTER_ID_BITS); // 最大支持数据中心节点数 31
    @SuppressWarnings({"PointlessBitwiseExpression", "FieldCanBeLocal"})
    protected static final long MAX_WORKER_ID = -1L ^ (-1L << WORKER_ID_BITS); // 最大支持机器节点数 31

    /**
     * 每一部分向左的位移
     */
    protected static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS; // 时间毫秒数左移22位
    protected static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 数据中心节点左移17位
    protected static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 机器节点左移12位

    // 默认最大抖动上限值
    protected static final int DEFAULT_VIBRATION_VALUE = 1;

    // 默认时钟回拨容忍度
    protected static final int MAX_TOLERATE_TIME_DIFFERENCE_MILLIS = 10;

    // 初始化时间点
    protected final long epoch = EPOCH;

    @Getter
    protected final long dataCenterId;

    @Getter
    protected final long workerId;

    // 最大抖动上限值，注意该值必须小于等于MAX_SEQUENCE即4095
    protected final int maxVibrationOffset;

    // 时钟回拨容忍度
    protected final int maxTolerateTimeDifferenceMillis;

    // 跨毫秒时的序列号，跨毫秒获取时该序列号+1，达到抖动上限会重置为0
    protected final AtomicInteger sequenceOffset = new AtomicInteger(-1);

    protected final AtomicLong sequence = new AtomicLong();

    protected final AtomicLong lastMillis = new AtomicLong();

    /**
     * @param dataCenterId                    数据中心 id
     * @param workerId                        工作机器节点 id
     * @param maxVibrationOffset              抖动上限值
     * @param maxTolerateTimeDifferenceMillis 时钟回拨容忍度
     */
    protected AbstractSnowflake(long dataCenterId, long workerId, int maxVibrationOffset, int maxTolerateTimeDifferenceMillis) {
        this.dataCenterId = Assert.checkBetween(dataCenterId, 0, MAX_DATA_CENTER_ID);
        this.workerId = Assert.checkBetween(workerId, 0, MAX_WORKER_ID);
        this.maxVibrationOffset = maxVibrationOffset;
        this.maxTolerateTimeDifferenceMillis = maxTolerateTimeDifferenceMillis;
    }

    @SneakyThrows(InterruptedException.class)
    protected boolean waitTolerateTimeDifferenceIfNeed(final long currentMillis) {
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

    protected long waitUntilNextTime(final long lastTime) {
        long result = genTime();
        while (result <= lastTime) {
            result = genTime();
        }
        return result;
    }

    protected void vibrateSequenceOffset() {
        if (!sequenceOffset.compareAndSet(maxVibrationOffset, 0)) {
            sequenceOffset.incrementAndGet();
        }
    }

    protected long genTime() {
        return System.currentTimeMillis();
    }
}
