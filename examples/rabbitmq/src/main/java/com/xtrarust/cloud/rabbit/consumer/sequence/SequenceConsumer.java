package com.xtrarust.cloud.rabbit.consumer.sequence;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xtrarust.cloud.rabbit.event.OrderClosedEvent;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.xtrarust.cloud.rabbit.config.RabbitConstants.*;

/**
 * 顺序消费
 *
 * 1.消费者单活：保证一个队列只有一个消费者
 * 2.消息键保序：开启多个队列，业务根据规则将消息分发到不同的队列，通过增加队列的数量来提高并发度
 */
@Slf4j
@Component
@RabbitListener(bindings = {
        @QueueBinding(
                value = @Queue(name = QUEUE_SEQUENCE_0, durable = "true", exclusive = "false", autoDelete = "false", arguments = {
                        @Argument(name = QUEUE_ARG_SINGLE_ACTIVE_CONSUMER, value = "true", type = "java.lang.Boolean")
                }),
                exchange = @Exchange(name = EXCHANGE_DIRECT),
                key = {KEY_SEQUENCE_0}
        ),
        @QueueBinding(
                value = @Queue(name = QUEUE_SEQUENCE_1, durable = "true", exclusive = "false", autoDelete = "false", arguments = {
                        @Argument(name = QUEUE_ARG_SINGLE_ACTIVE_CONSUMER, value = "true", type = "java.lang.Boolean")
                }),
                exchange = @Exchange(name = EXCHANGE_DIRECT),
                key = {KEY_SEQUENCE_1}
        ),
        @QueueBinding(
                value = @Queue(name = QUEUE_SEQUENCE_2, durable = "true", exclusive = "false", autoDelete = "false", arguments = {
                        @Argument(name = QUEUE_ARG_SINGLE_ACTIVE_CONSUMER, value = "true", type = "java.lang.Boolean")
                }),
                exchange = @Exchange(name = EXCHANGE_DIRECT),
                key = {KEY_SEQUENCE_2}
        ),
        @QueueBinding(
                value = @Queue(name = QUEUE_SEQUENCE_3, durable = "true", exclusive = "false", autoDelete = "false", arguments = {
                        @Argument(name = QUEUE_ARG_SINGLE_ACTIVE_CONSUMER, value = "true", type = "java.lang.Boolean")
                }),
                exchange = @Exchange(name = EXCHANGE_DIRECT),
                key = {KEY_SEQUENCE_3}
        ),
})
public class SequenceConsumer {

    @RabbitHandler
    public void onMessage(@Payload OrderCreateEvent event,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                          Channel channel) throws IOException {
        try {
            log.info("receive order create message: {}", JSON.toJSONString(event));
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.info("consume message error", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitHandler
    public void onMessage(@Payload OrderClosedEvent event,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                          Channel channel) throws IOException {
        try {
            log.info("receive order closed message: {}", JSON.toJSONString(event));
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.info("consume message error", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
