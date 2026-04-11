package com.xtrarust.cloud.id.core.provider;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@Slf4j
public class RedisSnowflakeNodeIdProvider implements SnowflakeNodeIdProvider {

    private static final String LUA_SCRIPT = """
            local hashKey = KEYS[1]
            local dataCenterIdField = ARGV[1]
            local workerIdField = ARGV[2]
            
            if (redis.call('exists', hashKey) == 0) then
                redis.call('hincrby', hashKey, dataCenterIdField, 0)
                redis.call('hincrby', hashKey, workerIdField, 0)
                return { 0, 0 }
            end
            
            local dataCenterId = tonumber(redis.call('hget', hashKey, dataCenterIdField))
            local workerId = tonumber(redis.call('hget', hashKey, workerIdField))
            
            local max = 31
            local resultDataCenterId = 0
            local resultWorkerId = 0
            
            if (dataCenterId == max and workerId == max) then
                redis.call('hset', hashKey, dataCenterIdField, '0')
                redis.call('hset', hashKey, workerIdField, '0')
            elseif (workerId ~= max) then
                resultDataCenterId = dataCenterId
                resultWorkerId = redis.call('hincrby', hashKey, workerIdField, 1)
            elseif (dataCenterId ~= max) then
                resultDataCenterId = redis.call('hincrby', hashKey, dataCenterIdField, 1)
                resultWorkerId = 0
                redis.call('hset', hashKey, workerIdField, '0')
            end
            
            return { resultDataCenterId, resultWorkerId }
            """;

    private static final String DATA_CENTER_ID_FIELD = "dataCenterId";

    private static final String WORKER_ID_FIELD = "workerId";

    private final String key;

    private final StringRedisTemplate stringRedisTemplate;

    public RedisSnowflakeNodeIdProvider(String key, StringRedisTemplate stringRedisTemplate) {
        this.key = key;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Pair<Long, Long> getNodeIdPair() {
        List<Long> resultList = null;
        try {
            DefaultRedisScript<List<Long>> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(LUA_SCRIPT);
            redisScript.setResultType((Class<List<Long>>) (Class<?>) List.class);
            resultList = this.stringRedisTemplate.execute(redisScript, List.of(key), DATA_CENTER_ID_FIELD, WORKER_ID_FIELD);
        } catch (Exception e) {
            throw new RuntimeException("Redis Lua 脚本获取 workerId 失败", e);
        }
        assert CollectionUtil.isNotEmpty(resultList);
        return Pair.of(resultList.get(0), resultList.get(1));
    }
}
