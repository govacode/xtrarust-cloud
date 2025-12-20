CREATE TABLE rabbit_message
(
    `id`           BIGINT       NOT NULL COMMENT '主键ID',
    `exchange`     VARCHAR(255) NOT NULL COMMENT '交换机',
    `routing_key`  VARCHAR(255)          DEFAULT NULL COMMENT '路由键',
    `message_body` TEXT         NOT NULL COMMENT '消息体JSON',
    `status`       TINYINT(1) NOT NULL COMMENT '状态：0-待发送，1-已发送，2-未正确路由，3-发送失败',
    `retry_num`    INT          NOT NULL DEFAULT '0' COMMENT '重试次数',
    `next_time`    datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下次驱动开始时间',
    `deleted`      TINYINT(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
    `create_by`    VARCHAR(50)           DEFAULT NULL COMMENT '创建人',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    VARCHAR(50)           DEFAULT NULL COMMENT '更新人',
    `update_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本地消息表'