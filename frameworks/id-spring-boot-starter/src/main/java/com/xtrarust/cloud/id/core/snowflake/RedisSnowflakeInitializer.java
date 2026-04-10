package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Redis 获取雪花 workerId
 */
@Slf4j
public class RedisSnowflakeInitializer extends AbstractSnowflakeInitializer {

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

    private static final String SNOWFLAKE_WORKER_ID_KEY = "snowflake_worker_id";

    private static final String DATA_CENTER_ID_FIELD = "dataCenterId";

    private static final String WORKER_ID_FIELD = "workerId";

    private final StringRedisTemplate stringRedisTemplate;

    private final RandomSnowflakeInitializer randomSnowflakeInitializer = new RandomSnowflakeInitializer();

    public RedisSnowflakeInitializer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @SuppressWarnings("all")
    public Pair<Long, Long> getWorkerId() {
        List<Long> luaResultList = null;
        try {
            DefaultRedisScript redisScript = new DefaultRedisScript();
            redisScript.setScriptText(LUA_SCRIPT);
            redisScript.setResultType(List.class);
            luaResultList = (ArrayList) this.stringRedisTemplate.execute(redisScript, List.of(SNOWFLAKE_WORKER_ID_KEY), DATA_CENTER_ID_FIELD, WORKER_ID_FIELD);
        } catch (Exception e) {
            log.error("Redis Lua 脚本获取 workerId 失败", e);
        }
        if (CollectionUtil.isNotEmpty(luaResultList)) {
            return Pair.of(luaResultList.get(0), luaResultList.get(1));
        }
        return randomSnowflakeInitializer.getWorkerId();
    }
}
