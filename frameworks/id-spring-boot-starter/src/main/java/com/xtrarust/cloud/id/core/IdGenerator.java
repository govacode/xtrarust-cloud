package com.xtrarust.cloud.id.core;

/**
 * ID 生成器
 */
public interface IdGenerator {

    /**
     * 下一个 ID
     */
    long nextId();

    /**
     * 下一个 ID（字符串形式）
     */
    default String nextIdStr() {
        return Long.toString(nextId());
    }
}
