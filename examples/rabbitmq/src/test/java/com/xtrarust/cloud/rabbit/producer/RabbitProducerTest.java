package com.xtrarust.cloud.rabbit.producer;

import com.xtrarust.cloud.rabbit.RabbitApplication;
import com.xtrarust.cloud.rabbit.event.OrderClosedEvent;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.xtrarust.cloud.rabbit.config.RabbitConstants.*;

@SpringBootTest(classes = RabbitApplication.class)
@Slf4j
class RabbitProducerTest {

    @Resource
    RabbitProducer rabbitProducer;

    @SneakyThrows
    @Test
    void testSendDirectExchange() {
        OrderCreateEvent event = new OrderCreateEvent().setOrderId(123456789L).setCreateTime(LocalDateTime.now());
        for (int i = 0; i < 10; i++) {
            rabbitProducer.send(EXCHANGE_DIRECT, KEY_DIRECT, event);
        }
        TimeUnit.SECONDS.sleep(2);
    }

    @SneakyThrows
    @Test
    void testSendTopicExchange() {
        OrderCreateEvent event = new OrderCreateEvent().setOrderId(123456789L);
        for (int i = 0; i < 10; i++) {
            rabbitProducer.send(EXCHANGE_TOPIC, "key.topic.x", event);
        }
        TimeUnit.SECONDS.sleep(2);
    }

    @SneakyThrows
    @Test
    void testSendFanoutExchange() {
        OrderCreateEvent event = new OrderCreateEvent().setOrderId(123456789L);
        rabbitProducer.send(EXCHANGE_FANOUT, null, event);
        TimeUnit.SECONDS.sleep(2);
    }

    @SneakyThrows
    @Test
    void testSendDelayMessage() {
        // 5s和2s的延迟消息都是5s后消费
        OrderCreateEvent event = new OrderCreateEvent().setOrderId(123456789L);
        rabbitProducer.sendDelayMessage(EXCHANGE_DIRECT, "key.ttl", event, 5, TimeUnit.SECONDS);
        rabbitProducer.sendDelayMessage(EXCHANGE_DIRECT, "key.ttl", event, 2, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
    }

    @SneakyThrows
    @Test
    void testSendDelayMessageWithPlugin() {
        // 延迟队列插件则不同 5s和2s的延迟消息分别是5s、2s后消费
        OrderCreateEvent event = new OrderCreateEvent().setOrderId(123456789L);
        // 延迟队列交换机类型
        rabbitProducer.sendDelayMessageWithPlugin(EXCHANGE_DELAY, KEY_DELAY, event, 5, TimeUnit.SECONDS);
        rabbitProducer.sendDelayMessageWithPlugin(EXCHANGE_DELAY, KEY_DELAY, event, 2, TimeUnit.SECONDS);
        TimeUnit.SECONDS.sleep(10);
    }

    @SneakyThrows
    @Test
    void testSendSequenceMessage() {
        long orderId1 = 100L;
        LocalDateTime createTime = LocalDateTime.now();
        LocalDateTime closedTime = createTime.plusSeconds(10);
        OrderCreateEvent createEvent = new OrderCreateEvent().setOrderId(orderId1).setCreateTime(createTime);
        OrderClosedEvent closedEvent = new OrderClosedEvent().setOrderId(orderId1).setClosedTime(closedTime);
        rabbitProducer.sendSequenceMessage(EXCHANGE_DIRECT, ROUTING_KEY_SEQUENCE, Long.toString(orderId1), 4, createEvent);
        rabbitProducer.sendSequenceMessage(EXCHANGE_DIRECT, ROUTING_KEY_SEQUENCE, Long.toString(orderId1), 4, closedEvent);

        long orderId2 = 101L;
        createEvent = new OrderCreateEvent().setOrderId(orderId2).setCreateTime(createTime);
        closedEvent = new OrderClosedEvent().setOrderId(orderId2).setClosedTime(closedTime);
        rabbitProducer.sendSequenceMessage(EXCHANGE_DIRECT, ROUTING_KEY_SEQUENCE, Long.toString(orderId2), 4, createEvent);
        rabbitProducer.sendSequenceMessage(EXCHANGE_DIRECT, ROUTING_KEY_SEQUENCE, Long.toString(orderId2), 4, closedEvent);
        TimeUnit.SECONDS.sleep(10);
    }
}