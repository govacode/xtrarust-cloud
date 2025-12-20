package com.xtrarust.cloud.rabbit.producer;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RabbitProducer {

    private static final String HEADER_X_DELAY = "x-delay";

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param exchange      交换机
     * @param routingKey    路由键
     * @param payload       消息
     */
    public void send(String exchange, String routingKey, Object payload) {
        // 消息默认持久化
        rabbitTemplate.convertAndSend(exchange, routingKey, payload);
    }

    /**
     * 发送消息
     *
     * @param exchange      交换机
     * @param routingKey    路由键
     * @param payload       消息
     * @param messageId     消息ID
     */
    public void send(String exchange, String routingKey, Object payload, String messageId) {
        rabbitTemplate.convertAndSend(exchange, routingKey, payload, new CorrelationData(messageId));
    }

    /**
     * 发送延迟消息
     *
     * @param exchange      交换机
     * @param routingKey    路由键
     * @param payload       消息
     * @param delay         延迟时间
     * @param timeUnit      时间单位
     */
    public void sendDelayMessage(String exchange, String routingKey, Object payload, long delay, TimeUnit timeUnit) {
        MessagePostProcessor postProcessor = message -> {
            if (delay > 0 && timeUnit != null) {
                message.getMessageProperties().setExpiration(String.valueOf(timeUnit.toMillis(delay)));
            }
            return message;
        };
        rabbitTemplate.convertAndSend(exchange, routingKey, payload, postProcessor);
    }

    /**
     * 利用延迟队列插件发送延迟消息
     *
     * @param exchange      交换机
     * @param routingKey    路由键
     * @param payload       消息
     * @param delay         延迟时间
     * @param timeUnit      时间单位
     */
    public void sendDelayMessageWithPlugin(String exchange, String routingKey, Object payload, long delay, TimeUnit timeUnit) {
        MessagePostProcessor postProcessor = message -> {
            if (delay > 0 && timeUnit != null) {
                message.getMessageProperties().setHeader(MessageProperties.X_DELAY, timeUnit.toMillis(delay));
            }
            return message;
        };
        rabbitTemplate.convertAndSend(exchange, routingKey, payload, postProcessor);
    }

    /**
     * 发送顺序消息（业务键保序）
     *
     * @param exchange      交换机
     * @param routingKey    路由键
     * @param messageKey    业务唯一键
     * @param queueNum      队列数
     * @param payload       消息
     */
    public void sendSequenceMessage(String exchange, String routingKey, String messageKey, Integer queueNum, Object payload) {
        String realRoutingKey = routingKey + messageKey.hashCode() % queueNum;
        log.info("sequence message routingKey: {}", realRoutingKey);
        rabbitTemplate.convertAndSend(exchange, realRoutingKey, payload);
    }
}
