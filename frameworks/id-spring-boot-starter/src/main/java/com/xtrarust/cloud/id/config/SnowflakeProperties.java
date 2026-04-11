package com.xtrarust.cloud.id.config;

import com.xtrarust.cloud.id.core.provider.SnowflakeNodeIdProviderType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 雪花{@code ID}属性配置类
 *
 * @author gova
 */
@Data
@ConfigurationProperties(prefix = "snowflake")
public class SnowflakeProperties {

    /**
     * 数据中心{@code ID}和工作机器节点{@code ID}提供者类型
     * <ul>
     *     <li>manual</li>
     *     <li>random</li>
     *     <li>redis</li>
     * </ul>
     */
    private SnowflakeNodeIdProviderType nodeIdProviderType = SnowflakeNodeIdProviderType.RANDOM;

    /**
     * redis key（分配策略为redis时填写，默认snowflake_node_id）
     */
    private String redisKey = "snowflake_node_id";

    /**
     * 数据中心{@code ID}（分配策略为manual时填写）
     */
    private Long manualDataCenterId;

    /**
     * 工作机器节点{@code ID}（分配策略为manual时填写）
     */
    private Long manualWorkerId;

    /**
     * 抖动上限
     */
    private Integer maxVibrationOffset = 1;

    /**
     * 时钟回拨容忍度（单位：毫秒）
     */
    private Integer maxTolerateTimeDifferenceMillis = 10;
}
