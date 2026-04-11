package com.xtrarust.cloud.id.core.provider;

public enum SnowflakeNodeIdProviderType {

    /**
     * 手动指定
     */
    MANUAL,
    /**
     * 随机
     */
    RANDOM,
    /**
     * Redis lua脚本获取
     */
    REDIS;
}
