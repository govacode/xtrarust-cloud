package com.xtrarust.cloud.rabbit.consumer.delay;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.xtrarust.cloud.rabbit.config.RabbitConstants.*;

@Slf4j
@Component
public class DelayMessageConsumer {

    @Configuration
    static class DelayQueueConfig {

        @Bean
        public org.springframework.amqp.core.Queue ttlQueue() {
            return QueueBuilder
                    .durable("queue.direct.ttl")
                    .ttl(10 * 1000) // 默认10s过期
                    // 由于消息监听器申明的 queue.direct.delay 队列绑定死信交换机 因此过期消息最终会被路由至 queue.direct.delay 队列中
                    // 以此模拟延迟队列
                    .deadLetterExchange(EXCHANGE_DLX)
                    .deadLetterRoutingKey(KEY_DELAY)
                    .build();
        }

        @Bean
        public org.springframework.amqp.core.Exchange directExchange() {
            return ExchangeBuilder.directExchange(EXCHANGE_DIRECT).build();
        }

        @Bean
        public Binding ttlBinding() {
            return BindingBuilder.bind(ttlQueue()).to(directExchange()).with("key.ttl").noargs();
        }
    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "queue.direct.delay", durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(name = EXCHANGE_DLX),
                    key = {KEY_DELAY}
            )
    })
    public void onMessage(Message message, Channel channel) throws IOException {
        final long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            OrderCreateEvent event = JSON.parseObject(message.getBody(), OrderCreateEvent.class);
            log.info("receive message: {}", JSON.toJSONString(event));

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.info("consume message error", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
