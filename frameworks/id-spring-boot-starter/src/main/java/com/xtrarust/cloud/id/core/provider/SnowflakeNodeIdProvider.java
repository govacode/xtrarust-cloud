package com.xtrarust.cloud.id.core.provider;

import org.apache.commons.lang3.tuple.Pair;

@FunctionalInterface
public interface SnowflakeNodeIdProvider {

    /**
     * 获取数据中心{@code ID}和工作节点{@code ID}
     *
     * @return 数据中心{@code ID}和工作节点{@code ID}对
     */
    Pair<Long, Long> getNodeIdPair();
}
