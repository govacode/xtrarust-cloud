package com.xtrarust.cloud.id.util;

import com.xtrarust.cloud.id.core.gene.GeneIdGeneratorManager;
import com.xtrarust.cloud.id.core.snowflake.Snowflake;
import com.xtrarust.cloud.id.core.snowflake.SnowflakeIdInfo;

/**
 * 分布式雪花 ID 生成器
 */
public final class SnowflakeIdUtil {

    /**
     * 雪花算法对象
     */
    private static Snowflake snowflake;

    public static long dataCenterId;

    public static long workerId;

    /**
     * 初始化雪花算法
     */
    public static void initSnowflake(Snowflake snowflake, long dataCenterId, long workerId) {
        SnowflakeIdUtil.snowflake = snowflake;
        SnowflakeIdUtil.dataCenterId = dataCenterId;
        SnowflakeIdUtil.workerId = workerId;
    }

    /**
     * 获取雪花算法实例
     */
    public static Snowflake getInstance() {
        return snowflake;
    }

    /**
     * 获取雪花算法下一个 ID
     */
    public static long nextId() {
        return snowflake.nextId();
    }

    /**
     * 获取雪花算法下一个字符串类型 ID
     */
    public static String nextIdStr() {
        return Long.toString(nextId());
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     */
    public static SnowflakeIdInfo parseSnowflakeId(String snowflakeId) {
        return snowflake.parseSnowflakeId(Long.parseLong(snowflakeId));
    }

    /**
     * 解析雪花算法生成的 ID 为对象
     */
    public static SnowflakeIdInfo parseSnowflakeId(long snowflakeId) {
        return snowflake.parseSnowflakeId(snowflakeId);
    }

    // --------------------------------- 基因法 ---------------------------------
    /**
     * 根据 {@param serviceId} 生成基因法雪花ID
     */
    public static long nextIdByServiceId(long geneBits, long serviceId) {
        return GeneIdGeneratorManager.getGeneIdGenerator(geneBits).nextId(serviceId);
    }

    /**
     * 解析基因法雪花ID
     */
    public static SnowflakeIdInfo parseSnowflakeServiceId(long geneBits, long snowflakeId) {
        return GeneIdGeneratorManager.getGeneIdGenerator(geneBits).parseSnowflakeId(snowflakeId);
    }

}
