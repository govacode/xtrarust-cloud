package com.xtrarust.cloud.sharding.mapper;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xtrarust.cloud.id.util.SnowflakeIdUtil;
import com.xtrarust.cloud.sharding.entity.Address;
import com.xtrarust.cloud.sharding.entity.Order;
import com.xtrarust.cloud.sharding.entity.OrderItem;
import com.xtrarust.cloud.sharding.enums.OrderStatusEnum;
import com.xtrarust.cloud.sharding.service.IAddressService;
import com.xtrarust.cloud.sharding.service.IOrderItemService;
import com.xtrarust.cloud.sharding.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest
class OrderMapperTest {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderItemService orderItemService;

    @Autowired
    private IAddressService addressService;

    @Test
    public void testId() {
        List<Long> userIds = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) {
            userIds.add(SnowflakeIdUtil.nextId());
        }

        Map<Long, Map<Long, AtomicInteger>> map = new HashMap<>();
        // 2 * 2 = 4 = 2^2
        Set<Long> orderIdSet = Sets.newHashSet();
        for (int i = 0; i < 10_0000; i++) { // 10w订单 1w用户 每个用户10个订单
            Long userId = userIds.get(i % 10000);
            long orderId = SnowflakeIdUtil.nextIdByServiceId(2, userId);
            orderIdSet.add(orderId);

            long shard = orderId % 4;
            long db = shard / 2;
            long tb = shard % 2;

            Map<Long, AtomicInteger> tbMap = map.computeIfAbsent(db, k -> new HashMap<>());
            tbMap.computeIfAbsent(tb, k -> new AtomicInteger(0)).incrementAndGet();
        }

        // countMap: {0={0=25000, 1=25000}, 1={0=25000, 1=25000}} 均匀分布
        log.info("countMap: {}", map);
        log.info("countSet: {}", orderIdSet.size());
    }

    @Test
    public void testSaveOrder() {
        List<String> mobileList = Lists.newArrayList("15611111111", "15622222222", "15633333333", "15644444444");

        List<Long> userIds = Lists.newArrayList();
        for (int i = 0; i < 10000; i++) { // 1w用户
            userIds.add(SnowflakeIdUtil.nextId());
        }

        for (int i = 0; i < 100000; i++) { // 10w订单 每个用户10个订单
            List<Order> orders = Lists.newArrayList();
            List<OrderItem> orderItems = Lists.newArrayList();

            Long userId = userIds.get(i % 10000);
            long orderId = SnowflakeIdUtil.nextIdByServiceId(2, userId);

            Order order = new Order();
            order.setOrderId(orderId);
            order.setShopId(1L);
            order.setUserId(userId);
            order.setMobile(mobileList.get(i % 4));
            order.setAddressId(2L);
            order.setTotalPrice(new BigDecimal("10.00"));
            order.setStatus(OrderStatusEnum.PENDING_PAYMENT);
            orders.add(order);

            OrderItem item1 = new OrderItem();
            item1.setOrderItemId(SnowflakeIdUtil.nextId());
            item1.setOrderId(orderId);
            item1.setUserId(userId);
            item1.setSkuId(10L);
            item1.setQuantity(2);
            item1.setPrice(new BigDecimal("2.50"));
            orderItems.add(item1);

            OrderItem item2 = new OrderItem();
            item2.setOrderItemId(SnowflakeIdUtil.nextId());
            item2.setOrderId(orderId);
            item2.setUserId(userId);
            item2.setSkuId(11L);
            item2.setQuantity(1);
            item2.setPrice(new BigDecimal("5.00"));
            orderItems.add(item2);

            orderService.saveBatch(orders);
            orderItemService.saveBatch(orderItems);
        }
    }

    @Test
    public void testQueryOrderByOrderId() {
        // Logic SQL: SELECT order_id,shop_id,user_id,mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order WHERE order_id=? AND deleted=0
        // Actual SQL: s_order0 ::: SELECT order_id,shop_id,user_id,encrypted_mobile AS mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order_1 WHERE order_id=? AND deleted=0 ::: [1122244958316732421]
        // 路由至 s_order0 库 t_order_1 表
        Order order = orderService.getById(1122244958316732421L);
        log.info("order: {}", order);

        // Logic SQL: SELECT order_item_id,order_id,user_id,sku_id,quantity,price,deleted,create_by,create_time,update_by,update_time FROM t_order_item WHERE deleted=0 AND (order_id = ?)
        // Actual SQL: s_order0 ::: SELECT order_item_id,order_id,user_id,sku_id,quantity,price,deleted,create_by,create_time,update_by,update_time FROM t_order_item_1 WHERE deleted=0 AND (order_id = ?) ::: [1122244958316732421]
        // 路由至 s_order0 库 t_order_item_1 表
        List<OrderItem> items = orderItemService.lambdaQuery().eq(OrderItem::getOrderId, 1122244958316732421L).list();
        log.info("order items: {}", items);
    }

    @Test
    public void testQueryOrderByUserId() {
        // Logic SQL: SELECT order_id,shop_id,user_id,mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order WHERE deleted=0 AND (user_id = ?) ORDER BY order_id ASC
        // Actual SQL: s_order0 ::: SELECT order_id,shop_id,user_id,encrypted_mobile AS mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order_1 WHERE deleted=0 AND (user_id = ?) ORDER BY order_id ASC ::: [1122244948850188297]
        // 路由至 s_order0 库 t_order_1 表
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, 1122244948850188297L)
                .orderByAsc(Order::getOrderId);
        List<Order> orders = orderService.list(queryWrapper);
        log.info("query by userId, count: {}, orders: {}", orders.size(), orders);
    }

    @Test
    public void testLogicDeleteOrderByOrderId() {
        // 路由至 m_order1 库 t_order_0 表
        log.info("logic delete order by id success ? {}", orderService.removeById(1122244958216069122L));

        // 路由至 m_order1 库 t_order_item_0 表
        orderItemService.remove(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, 1122244958216069122L));
    }

    @Test
    public void testUpdateOrderStatusByOrderId() {
        // Logic SQL: UPDATE t_order SET status=? WHERE deleted=0 AND (order_id = ?)
        // Actual SQL: m_order1 ::: UPDATE t_order_0 SET status=? WHERE deleted=0 AND (order_id = ?) ::: [PAID, 1122244958216069122]
        // 路由至 m_order1 库 t_order_0 表
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<Order>()
                .eq(Order::getOrderId, 1122244958216069122L)
                .set(Order::getStatus, OrderStatusEnum.PAID);
        orderService.update(updateWrapper);
    }

    @Test
    public void testQueryOrderByMobile() {
        // 非分片键全路由 加密字段默认使用密文列进行查询
        List<Order> orders = orderService.lambdaQuery().eq(Order::getMobile, "15622222222").list();
        log.info("order info: {}", orders);
    }

    // 开发加密、数据清洗、切换密文列、删除明文字段
    // 加密列数据清洗
    @Test
    public void testUpdateMobile() {
        orderService.update(new LambdaUpdateWrapper<Order>()
                .eq(Order::getOrderId, 42461334203181318L)
                .set(Order::getMobile, "18322222222"));
    }

    @Test
    public void testQueryOrderByOrderIdRange() {
        // 自定义分片算法暂不支持范围查询
        orderService.list(new LambdaQueryWrapper<Order>().gt(Order::getOrderId, 0L));
    }

    @Test
    public void testQueryOrderByUserIdRange() {
        orderService.list(new LambdaQueryWrapper<Order>().between(Order::getUserId, 1L, 5L));
    }

    @Test
    public void testPageQueryByUserId() {
        // Logic SQL: SELECT COUNT(*) AS total FROM t_order WHERE deleted = 0 AND (user_id = ?)
        // Actual SQL: s_order0 ::: SELECT COUNT(*) AS total FROM t_order_0 WHERE deleted = 0 AND (user_id = ?) ::: [1122244948850188340]
        // Logic SQL: SELECT order_id,shop_id,user_id,mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order WHERE deleted=0 AND (user_id = ?) ORDER BY create_time DESC LIMIT ?
        // Actual SQL: s_order0 ::: SELECT order_id,shop_id,user_id,encrypted_mobile AS mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order_0 WHERE deleted=0 AND (user_id = ?) ORDER BY create_time DESC LIMIT ? ::: [1122244948850188340, 5]
        // 路由至 s_order0 库 t_order_0 表
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, 1122244948850188340L)
                .orderByDesc(Order::getCreateTime);
        Page<Order> page = orderService.page(new Page<>(1, 5), queryWrapper);
        log.info("page query result: \n{}", JSON.toJSONString(page, true));

        // Actual SQL: s_order0 ::: SELECT order_id,shop_id,user_id,encrypted_mobile AS mobile,address_id,total_price,status,deleted,create_by,create_time,update_by,update_time FROM t_order_0 WHERE deleted=0 AND (user_id = ?) ORDER BY create_time DESC LIMIT ?,? ::: [1122244948850188340, 5, 5]
        page = orderService.page(new Page<>(2, 5), queryWrapper);
        log.info("page query result: \n{}", JSON.toJSONString(page, true));
    }

    @Test
    public void testCountOrder() {
        try (HintManager hintManager = HintManager.getInstance()) {
            hintManager.setWriteRouteOnly(); // 强制走主库
            // Logic SQL: SELECT COUNT(*) AS total FROM t_order WHERE deleted=0 AND (user_id = ?)
            // Actual SQL: m_order0 ::: SELECT COUNT(*) AS total FROM t_order_1 WHERE deleted=0 AND (user_id = ?) ::: [1122244948850188333]
            log.info("order count: {}", orderService.count(new LambdaQueryWrapper<Order>().eq(Order::getUserId, 1122244948850188333L)));
        }
    }

    @Test
    public void testGroupByUserId() {
        // Logic SQL: SELECT user_id,COUNT(*) AS count FROM t_order WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order0 ::: SELECT user_id,COUNT(*) AS count FROM t_order_0 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order0 ::: SELECT user_id,COUNT(*) AS count FROM t_order_1 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order0 ::: SELECT user_id,COUNT(*) AS count FROM t_order_2 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order0 ::: SELECT user_id,COUNT(*) AS count FROM t_order_3 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order1 ::: SELECT user_id,COUNT(*) AS count FROM t_order_0 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order1 ::: SELECT user_id,COUNT(*) AS count FROM t_order_1 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order1 ::: SELECT user_id,COUNT(*) AS count FROM t_order_2 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        // Actual SQL: s_order1 ::: SELECT user_id,COUNT(*) AS count FROM t_order_3 WHERE deleted=0 GROUP BY user_id ORDER BY count ASC
        QueryWrapper<Order> queryWrapper = new QueryWrapper<Order>()
                .select("user_id", "COUNT(*) AS count")
                .groupBy("user_id")
                .orderByAsc("count");
        List<Map<String, Object>> orderCountList = orderService.listMaps(queryWrapper);
        orderCountList.forEach(e -> log.info("userId: {} orderCount: {}", e.get("user_id"), e.get("count")));
    }

    // 全局表 t_address
    // sharding-jdbc每个库中都插入一份数据 主库插入后同步至从库
    // Logic SQL: INSERT INTO t_address  ( address_id, address_name, create_by, create_time, update_by, update_time )  VALUES  ( ?, ?, ?, ?, ?, ? )
    // Actual SQL: m_order0 ::: INSERT INTO t_address  ( address_id, address_name, create_by, create_time, update_by, update_time )  VALUES  (?, ?, ?, ?, ?, ?) ::: [1, 台湾, admin, 2022-08-05T13:16:42.049, admin, 2022-08-05T13:16:42.049]
    // Actual SQL: m_order1 ::: INSERT INTO t_address  ( address_id, address_name, create_by, create_time, update_by, update_time )  VALUES  (?, ?, ?, ?, ?, ?) ::: [1, 台湾, admin, 2022-08-05T13:16:42.049, admin, 2022-08-05T13:16:42.049]
    @Test
    public void testSaveAddress() {
        addressService.save(Address.builder().addressId(1L).addressName("台湾").build());
        addressService.save(Address.builder().addressId(2L).addressName("美利坚合众国").build());
    }

    // 只会查询一个库
    @Test
    // Logic SQL: SELECT address_id,address_name,deleted,create_by,create_time,update_by,update_time FROM t_address WHERE address_id IN (   ?  ,  ?  ) AND deleted=0
    // Actual SQL: s_order1 ::: SELECT address_id,address_name,deleted,create_by,create_time,update_by,update_time FROM t_address WHERE address_id IN (   ?  ,  ?  ) AND deleted=0 ::: [1, 2]
    public void testQueryAddress() {
        log.info("query result: {}", addressService.listByIds(Arrays.asList(1L, 2L)));
    }

}