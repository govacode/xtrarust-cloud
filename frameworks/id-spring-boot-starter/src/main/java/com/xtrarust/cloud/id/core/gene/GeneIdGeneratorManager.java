package com.xtrarust.cloud.id.core.gene;

import com.xtrarust.cloud.id.core.GeneIdGenerator;
import com.xtrarust.cloud.id.util.SnowflakeIdUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ID 生成器管理
 */
public final class GeneIdGeneratorManager {

    private static final Map<Long, GeneIdGenerator> MANAGER = new ConcurrentHashMap<>();

    public static GeneIdGenerator getGeneIdGenerator(long geneBits) {
        return MANAGER.computeIfAbsent(
                geneBits,
                k -> new SnowflakeGeneIdGenerator(SnowflakeIdUtil.dataCenterId, SnowflakeIdUtil.workerId, geneBits)
        );
    }
}
