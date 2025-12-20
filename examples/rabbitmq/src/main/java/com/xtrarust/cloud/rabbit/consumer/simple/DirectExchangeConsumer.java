package com.xtrarust.cloud.rabbit.consumer.simple;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xtrarust.cloud.rabbit.event.OrderCreateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.xtrarust.cloud.rabbit.config.RabbitConstants.*;

@Component
@Slf4j
public class DirectExchangeConsumer {

    // The following list shows the arguments that are available to be matched with parameters in listener endpoints:
    //• The raw org.springframework.amqp.core.Message.
    //• The MessageProperties from the raw Message.
    //• The com.rabbitmq.client.Channel on which the message was received.
    //• The org.springframework.messaging.Message converted from the incoming AMQP message.
    //• @Header-annotated method arguments to extract a specific header value, including standard AMQP headers.
    //• @Headers-annotated argument that must also be assignable to java.util.Map for getting access to all headers.
    //• The converted payload
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = QUEUE_DIRECT, durable = "true", exclusive = "false", autoDelete = "false"),
                    exchange = @Exchange(name = EXCHANGE_DIRECT),
                    key = {KEY_DIRECT}
            )
    })
    public void onMessage(@Payload OrderCreateEvent event,
                          @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                          Channel channel) throws IOException {
        try {
            log.info("receive message: {}", JSON.toJSONString(event));
            int i = 1 / 0;

            // 第二个参数 multiple ，用于批量确认消息，为了减少网络流量，手动确认可以被批处。
            // 1. 当 multiple 为 true 时，则可以一次性确认 deliveryTag 小于等于传入值的所有消息
            // 2. 当 multiple 为 false 时，则只确认当前 deliveryTag 对应的消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.info("consume message error", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
