package com.xtrarust.cloud.rabbit.consumer.delay;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.xtrarust.cloud.rabbit.config.RabbitConstants.*;

@Component
@Slf4j
public class PluginDelayMessageConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    // 声明延迟队列
                    value = @Queue(name = QUEUE_DELAY, durable = "true", exclusive = "false", autoDelete = "false"),
                    // 声明延迟交换机 x-delayed-message
                    exchange = @Exchange(name = EXCHANGE_DELAY, type = EXCHANGE_TYPE_DELAY, arguments = {
                            @Argument(name = DELAY_TYPE, value = ExchangeTypes.DIRECT)
                    }),
                    key = {KEY_DELAY}
            )
    })
    public void onMessage(Message message, Channel channel) throws IOException {
        try {
            OrderCreateEvent event = JSON.parseObject(message.getBody(), OrderCreateEvent.class);
            log.info("receive delay message: {}", JSON.toJSONString(event));
            final long deliveryTag = message.getMessageProperties().getDeliveryTag();

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.info("consume message error", e);
            channel.basicRecover(false);
        }
    }
}
