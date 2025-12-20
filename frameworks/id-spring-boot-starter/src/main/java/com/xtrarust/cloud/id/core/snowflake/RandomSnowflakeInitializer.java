package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 随机获取雪花 workerId
 */
@Slf4j
public class RandomSnowflakeInitializer extends AbstractSnowflakeInitializer {

    @Override
    public Pair<Long, Long> getWorkerId() {
        long dataCenterId = IdUtil.getDataCenterId(31);
        long workerId = IdUtil.getWorkerId(dataCenterId, 31);
        return Pair.of(dataCenterId, workerId);
    }
}
