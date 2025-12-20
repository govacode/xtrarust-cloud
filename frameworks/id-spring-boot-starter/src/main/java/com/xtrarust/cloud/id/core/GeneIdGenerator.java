package com.xtrarust.cloud.id.core;

import com.xtrarust.cloud.id.core.snowflake.SnowflakeIdInfo;

/**
 * ID 生成器（基因法）
 *
 * @author gova
 */
public interface GeneIdGenerator {

    /**
     * 根据 {@param serviceId} 生成雪花算法 ID
     */
    long nextId(long serviceId);

    /**
     * 根据 {@param serviceId} 生成字符串类型雪花算法 ID
     */
    default String nextIdStr(long serviceId) {
        return Long.toString(nextId(serviceId));
    }

    /**
     * 解析雪花ID
     */
    SnowflakeIdInfo parseSnowflakeId(long snowflakeId);
}
