-- 订单表分片数 2 * 2
CREATE DATABASE `db_order0`;

CREATE TABLE `t_order_0` (
    `order_id` bigint NOT NULL COMMENT '订单id',
    `shop_id` bigint NOT NULL COMMENT '店铺id',
    `user_id` bigint NOT NULL COMMENT '用户id',
    `mobile` varchar(64) DEFAULT NULL COMMENT '手机号',
    `encrypted_mobile` varchar(64) NOT NULL COMMENT '加密手机号',
    `address_id` bigint NOT NULL COMMENT '地址id',
    `total_price` decimal(10,2) NOT NULL COMMENT '订单总金额',
    `status` varchar(64) NOT NULL COMMENT '订单状态',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表0';

CREATE TABLE `t_order_1` (
    `order_id` bigint NOT NULL COMMENT '订单id',
    `shop_id` bigint NOT NULL COMMENT '店铺id',
    `user_id` bigint NOT NULL COMMENT '用户id',
    `mobile` varchar(64) DEFAULT NULL COMMENT '手机号',
    `encrypted_mobile` varchar(64) NOT NULL COMMENT '加密手机号',
    `address_id` bigint NOT NULL COMMENT '地址id',
    `total_price` decimal(10,2) NOT NULL COMMENT '订单总金额',
    `status` varchar(64) NOT NULL COMMENT '订单状态',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`order_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表1';

CREATE TABLE `t_order_item_0` (
    `order_item_id` bigint NOT NULL COMMENT '订单项id',
    `order_id` bigint NOT NULL COMMENT '所属订单id',
    `user_id` bigint NOT NULL COMMENT '用户id',
    `sku_id` bigint NOT NULL COMMENT '商品id',
    `quantity` int NOT NULL COMMENT '商品数量',
    `price` decimal(10,2) NOT NULL COMMENT '金额',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项子表0';

CREATE TABLE `t_order_item_1` (
    `order_item_id` bigint NOT NULL COMMENT '订单项id',
    `order_id` bigint NOT NULL COMMENT '所属订单id',
    `user_id` bigint NOT NULL COMMENT '用户id',
    `sku_id` bigint NOT NULL COMMENT '商品id',
    `quantity` int NOT NULL COMMENT '商品数量',
    `price` decimal(10,2) NOT NULL COMMENT '金额',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`order_item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项子表1';

CREATE TABLE `t_address` (
    `address_id` bigint NOT NULL COMMENT '地址id',
    `address_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '地址描述',
    `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bigint NOT NULL DEFAULT '0' COMMENT '是否删除',
    PRIMARY KEY (`address_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地址表';

-- db_order1 表结构同 db_order0
CREATE DATABASE `db_order1`;