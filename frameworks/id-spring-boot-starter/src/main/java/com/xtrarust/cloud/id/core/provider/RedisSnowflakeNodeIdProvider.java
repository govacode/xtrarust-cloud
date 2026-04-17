package com.xtrarust.cloud.id.core.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

@Slf4j
public class RedisSnowflakeNodeIdProvider implements SnowflakeNodeIdProvider {

    private static final String DATA_CENTER_ID_FIELD = "dataCenterId";

    private static final String WORKER_ID_FIELD = "workerId";

    private static final String MAX_ID = "31";

    private final String key;

    private final RedisScript<List<Long>> redisScript;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisSnowflakeNodeIdProvider(String key,
                                        RedisScript<List<Long>> redisScript,
                                        StringRedisTemplate stringRedisTemplate) {
        this.key = key;
        this.redisScript = redisScript;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Pair<Long, Long> getNodeIdPair() {
        List<Long> resultList;
        try {
            resultList = this.stringRedisTemplate.execute(this.redisScript, List.of(key), DATA_CENTER_ID_FIELD, WORKER_ID_FIELD, MAX_ID);
        } catch (Exception e) {
            throw new RuntimeException("Redis Lua 脚本获取 workerId 失败", e);
        }
        assert CollectionUtil.isNotEmpty(resultList);
        return Pair.of(resultList.get(0), resultList.get(1));
    }
}
