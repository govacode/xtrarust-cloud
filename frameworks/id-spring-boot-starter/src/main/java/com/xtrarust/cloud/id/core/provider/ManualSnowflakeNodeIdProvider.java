package com.xtrarust.cloud.id.core.provider;

import cn.hutool.core.lang.Assert;
import org.apache.commons.lang3.tuple.Pair;

public class ManualSnowflakeNodeIdProvider implements SnowflakeNodeIdProvider {

    private final long dataCenterId;

    private final long workerId;

    public ManualSnowflakeNodeIdProvider(long dataCenterId, long workerId) {
        this.dataCenterId = Assert.notNull(dataCenterId);
        this.workerId = Assert.notNull(workerId);
    }

    @Override
    public Pair<Long, Long> getNodeIdPair() {
        return Pair.of(this.dataCenterId, this.workerId);
    }
}
