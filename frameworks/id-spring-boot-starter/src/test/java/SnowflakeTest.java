import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xtrarust.cloud.id.core.snowflake.Snowflake;
import com.xtrarust.cloud.id.util.SnowflakeIdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SnowflakeTest {

    @Test
    public void testGeneId() {
        Snowflake snowflake = new Snowflake(5, 9);
        SnowflakeIdUtil.initSnowflake(snowflake);

        List<Long> userIds = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) {
            userIds.add(SnowflakeIdUtil.nextId());
        }

        Map<Long, Map<Long, AtomicInteger>> map = new HashMap<>();
        // 总表数 = 4个库 16张表/库 = 64 = 2^6
        // 数据库：db_0, db_1, db_2, db_3
        //   ├── db_0
        //   │   ├── t_order_0 到 t_order_15
        //   ├── db_1
        //   │   ├── t_order_0 到 t_order_15
        //   ├── db_2
        //   │   ├── t_order_0 到 t_order_15
        //   └── db_3
        //       ├── t_order_0 到 t_order_15
        final int DB_BITS = 2;       // 2² = 4个库
        final int TABLE_BITS = 4;    // 2⁴ = 16张表/库
        final int TABLE_MASK = (1 << TABLE_BITS) - 1;

        Set<Long> orderIdSet = Sets.newHashSet();
        for (int i = 0; i < 100_000; i++) { // 10w订单 1w用户 每个用户10个订单
            Long userId = userIds.get(i % 10000);
            long orderId = SnowflakeIdUtil.nextGeneId(6, userId);
            orderIdSet.add(orderId);

            // long shard = orderId & ((1L << 6) - 1); // 计算总分片位 orderId % 64
            // long db = shard >> 4; // 确定数据库 除以每库表数 shard / 16
            // long tb = shard & ((1L << 4) - 1); // 确定表 模每库表数 shard % 16
            long db = (orderId >> TABLE_BITS) & ((1 << DB_BITS) - 1);
            long tb = orderId & TABLE_MASK;

            // 均匀分布
            // {0={0=1570, 1=1570, 2=1570, 3=1570, 4=1570, 5=1570, 6=1570, 7=1570, 8=1570, 9=1570, 10=1570, 11=1570, 12=1570, 13=1570, 14=1570, 15=1570},
            // 1={0=1560, 1=1560, 2=1560, 3=1560, 4=1560, 5=1560, 6=1560, 7=1560, 8=1560, 9=1560, 10=1560, 11=1560, 12=1560, 13=1560, 14=1560, 15=1560},
            // 2={0=1560, 1=1560, 2=1560, 3=1560, 4=1560, 5=1560, 6=1560, 7=1560, 8=1560, 9=1560, 10=1560, 11=1560, 12=1560, 13=1560, 14=1560, 15=1560},
            // 3={0=1560, 1=1560, 2=1560, 3=1560, 4=1560, 5=1560, 6=1560, 7=1560, 8=1560, 9=1560, 10=1560, 11=1560, 12=1560, 13=1560, 14=1560, 15=1560}}
            map.computeIfAbsent(db, k -> new HashMap<>())
                    .computeIfAbsent(tb, k -> new AtomicInteger(0)).incrementAndGet();
        }

        log.info("countMap: {}", map);
        log.info("countSet: {}", orderIdSet.size());
    }
}
