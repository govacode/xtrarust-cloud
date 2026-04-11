package com.xtrarust.cloud.id.core.provider;

import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ThreadLocalRandom;

public class RandomSnowflakeNodeIdProvider implements SnowflakeNodeIdProvider {

    @Override
    public Pair<Long, Long> getNodeIdPair() {
        long dataCenterId = ThreadLocalRandom.current().nextLong(32);
        long workerId = ThreadLocalRandom.current().nextLong(32);
        return Pair.of(dataCenterId, workerId);
    }
}
