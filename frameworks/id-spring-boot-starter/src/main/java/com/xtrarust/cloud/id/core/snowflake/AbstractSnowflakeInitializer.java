package com.xtrarust.cloud.id.core.snowflake;

import cn.hutool.core.date.SystemClock;
import com.xtrarust.cloud.id.util.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * 雪花算法模板生成
 */
@Slf4j
public abstract class AbstractSnowflakeInitializer implements SnowflakeInitializer, InitializingBean {

    /**
     * 是否使用 {@link SystemClock} 获取当前时间戳
     */
    @Value("${framework.distributed-id.snowflake.use-system-clock:false}")
    private boolean isUseSystemClock;

    /**
     * 选择 WorkId 并初始化雪花
     */
    @Override
    public void initSnowflake() {
        // 模板方法模式: 通过抽象方法获取 WorkId 包装器创建雪花算法
        Pair<Long, Long> pair = getWorkerId();
        long dataCenterId = pair.getLeft(), workerId = pair.getRight();
        Snowflake snowflake = new Snowflake(dataCenterId, workerId, isUseSystemClock);
        log.info(">>>>>> Snowflake Initializer: {}, dataCenterId: {}, workerId: {}", this.getClass().getSimpleName(), dataCenterId, workerId);
        SnowflakeIdUtil.initSnowflake(snowflake, dataCenterId, workerId);
    }

    /**
     * 根据自定义策略获取 Pair<dataCenterId, workerId>
     */
    protected abstract Pair<Long, Long> getWorkerId();

    @Override
    public void afterPropertiesSet() throws Exception {
        initSnowflake();
    }
}
