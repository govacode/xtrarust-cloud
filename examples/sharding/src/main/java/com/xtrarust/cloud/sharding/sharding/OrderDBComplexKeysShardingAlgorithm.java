package com.xtrarust.cloud.sharding.sharding;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.sharding.api.sharding.complex.ComplexKeysShardingValue;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 自定义订单库水平分库复合分片算法
 *
 * @author gova
 */
public final class OrderDBComplexKeysShardingAlgorithm implements ComplexKeysShardingAlgorithm<Long> {

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> shardingValue) {
        // 范围查询的分片键值集合:userId/orderId
        Map<String, Range<Long>> shardingRangeMaps = shardingValue.getColumnNameAndRangeValuesMap();
        // TODO 范围查询暂时不支持，后续可以自己业务需求自己实现
        if (!shardingRangeMaps.isEmpty()) {
            throw new UnsupportedOperationException("只支持精确查询，不支持范围查询");
        }
        // 精确查询的分片键值集合
        Map<String, Collection<Long>> shardingMaps = shardingValue.getColumnNameAndShardingValuesMap();

        Collection<Long> userIds = shardingMaps.getOrDefault(ShardingColumns.USER_ID, Collections.emptyList());
        Collection<Long> orderIds = shardingMaps.getOrDefault(ShardingColumns.ORDER_ID, Collections.emptyList());

        List<Long> ids = Lists.newArrayList();
        ids.addAll(orderIds);
        ids.addAll(userIds);

        return ids.stream()
                .map(id -> MessageFormat.format("order{0}", (id % 4) / availableTargetNames.size()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getType() {
        return "ORDER_DB_COMPLEX";
    }

}
