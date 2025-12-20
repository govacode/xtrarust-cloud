package com.xtrarust.cloud.rabbit.consumer.simple;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.xtrarust.cloud.rabbit.config.RabbitConstants.EXCHANGE_FANOUT;
import static com.xtrarust.cloud.rabbit.config.RabbitConstants.QUEUE_FANOUT_1;

@Component
@Slf4j
public class FanoutExchangeConsumer {

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = QUEUE_FANOUT_1, durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(name = EXCHANGE_FANOUT, type = ExchangeTypes.FANOUT)
            )
    })
    public void onMessage(@Payload OrderCreateEvent event,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                          Channel channel) throws IOException {
        try {
            log.info("receive message: {}", JSON.toJSONString(event));

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.info("consume message error", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
