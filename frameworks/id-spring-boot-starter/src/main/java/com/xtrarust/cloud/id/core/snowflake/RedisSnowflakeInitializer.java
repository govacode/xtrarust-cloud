package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.ArrayList;
import java.util.List;

/**
 * 使用 Redis 获取雪花 workerId
 */
@Slf4j
@SuppressWarnings("all")
public class RedisSnowflakeInitializer extends AbstractSnowflakeInitializer {

    private static final String SNOWFLAKE_WORKER_ID_KEY = "snowflake_worker_id";

    private static final String DATA_CENTER_ID_FIELD = "dataCenterId";

    private static final String WORKER_ID_FIELD = "workerId";

    private final StringRedisTemplate stringRedisTemplate;

    private final RandomSnowflakeInitializer randomSnowflakeInitializer = new RandomSnowflakeInitializer();

    public RedisSnowflakeInitializer(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Pair<Long, Long> getWorkerId() {
        DefaultRedisScript redisScript = new DefaultRedisScript();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("script/snowflake_worker_id.lua")));
        List<Long> luaResultList = null;
        try {
            redisScript.setResultType(List.class);
            luaResultList = (ArrayList) this.stringRedisTemplate.execute(redisScript, List.of(SNOWFLAKE_WORKER_ID_KEY), DATA_CENTER_ID_FIELD, WORKER_ID_FIELD);
        } catch (Exception ex) {
            log.error("Redis Lua 脚本获取 workerId 失败", ex);
        }
        if (CollectionUtil.isNotEmpty(luaResultList)) {
            return Pair.of(luaResultList.get(0), luaResultList.get(1));
        }
        return randomSnowflakeInitializer.getWorkerId();
    }
}
