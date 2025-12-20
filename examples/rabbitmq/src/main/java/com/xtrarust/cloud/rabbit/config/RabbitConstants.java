package com.xtrarust.cloud.rabbit.config;

public final class RabbitConstants {

    // 消息发送最大重试次数
    public static final int MAX_RETRY_COUNT = 3;

    public static final String EXCHANGE_DIRECT = "exchange.direct";

    public static final String QUEUE_DIRECT = "queue.direct";

    public static final String KEY_DIRECT = "key.direct";

    public static final String EXCHANGE_TOPIC = "exchange.topic";

    public static final String QUEUE_TOPIC = "queue.topic";

    public static final String KEY_TOPIC = "key.topic.*";

    public static final String EXCHANGE_FANOUT = "exchange.fanout";

    public static final String QUEUE_FANOUT_1 = "queue.fanout_1";

    public static final String QUEUE_FANOUT_2 = "queue.fanout_2";

    // 死信交换机
    public static final String EXCHANGE_DLX = "exchange.dlx";

    // 延迟队列相关
    public static final String EXCHANGE_DELAY = "exchange.delay";

    public static final String QUEUE_DELAY = "queue.delay";

    public static final String KEY_DELAY = "key.delay";

    // 延迟队列交换机类型
    public static final String EXCHANGE_TYPE_DELAY = "x-delayed-message";

    public static final String DELAY_TYPE = "x-delayed-type";

    // 顺序消息相关 多个队列用于提升顺序消费并发度
    public static final String QUEUE_SEQUENCE_0 = "queue.sequence_0";

    public static final String KEY_SEQUENCE_0 = "key.sequence_0";

    public static final String QUEUE_SEQUENCE_1 = "queue.sequence_1";

    public static final String KEY_SEQUENCE_1 = "key.sequence_1";

    public static final String QUEUE_SEQUENCE_2 = "queue.sequence_2";

    public static final String KEY_SEQUENCE_2 = "key.sequence_2";

    public static final String QUEUE_SEQUENCE_3 = "queue.sequence_3";

    public static final String KEY_SEQUENCE_3 = "key.sequence_3";

    public static final String ROUTING_KEY_SEQUENCE = "key.sequence_";

    // 队列参数：消费者单活
    // If set, makes sure only one consumer at a time consumes from the queue and fails over to another registered consumer in case the active one is cancelled or dies.
    public static final String QUEUE_ARG_SINGLE_ACTIVE_CONSUMER = "x-single-active-consumer";

}
