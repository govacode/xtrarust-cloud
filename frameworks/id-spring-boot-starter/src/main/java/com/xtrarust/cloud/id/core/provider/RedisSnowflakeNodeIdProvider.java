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
            local maxId = tonumber(ARGV[3]) or 31
            
            -- 初始化或获取当前值
            local dataCenterId = redis.call('hincrby', hashKey, dataCenterIdField, 0)
            local workerId = redis.call('hincrby', hashKey, workerIdField, 0)
            
            -- 全部用完重置
            if dataCenterId == maxId and workerId == maxId then
                redis.call('hset', hashKey, dataCenterIdField, 0)
                redis.call('hset', hashKey, workerIdField, 0)
                return {0, 0}
            end
            
            -- 递增 workerId
            if workerId < maxId then
                workerId = redis.call('hincrby', hashKey, workerIdField, 1)
                return {dataCenterId, workerId}
            end
            
            -- workerId 已满，进位到 datacenterId（此时 dataCenterId 必然 < maxId）
            dataCenterId = redis.call('hincrby', hashKey, dataCenterIdField, 1)
            redis.call('hset', hashKey, workerIdField, 0)
            return {dataCenterId, 0}
            """;

    private static final String DATA_CENTER_ID_FIELD = "dataCenterId";

    private static final String WORKER_ID_FIELD = "workerId";

    private static final String MAX_ID = "31";

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
            resultList = this.stringRedisTemplate.execute(redisScript, List.of(key), DATA_CENTER_ID_FIELD, WORKER_ID_FIELD, MAX_ID);
        } catch (Exception e) {
            throw new RuntimeException("Redis Lua 脚本获取 workerId 失败", e);
        }
        assert CollectionUtil.isNotEmpty(resultList);
        return Pair.of(resultList.get(0), resultList.get(1));
    }
}
