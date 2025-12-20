package com.xtrarust.cloud.rabbit.config;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.xtrarust.cloud.rabbit.service.RabbitMessageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * RabbitAutoConfiguration -> import RabbitAnnotationDrivenConfiguration -> @EnableRabbit -> import RabbitListenerConfigurationSelector
 * -> RabbitBootstrapConfiguration -> 注册 RabbitListenerAnnotationBeanPostProcessor 及 RabbitListenerEndpointRegistry
 * RabbitListenerAnnotationBeanPostProcessor
 * -> postProcessAfterInitialization 解析 RabbitListener 最终调用 RabbitListenerEndpointRegistrar#registerEndpoint（添加到endpointDescriptors list集合）
 * -> afterSingletonsInstantiated -> registrar.afterPropertiesSet() 注册监听器（遍历endpointDescriptors向RabbitListenerEndpointRegistry中注册RabbitListenerContainerFactory）
 * 最终所有的listenerContainers被RabbitListenerEndpointRegistry持有
 * 如果需要动态修改并发消费线程数可以遍历listenerContainers 调用 SimpleMessageListenerContainer 中设置线程数方法
 *
 * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
 * @see org.springframework.amqp.rabbit.annotation.EnableRabbit
 */
@Slf4j
@Configuration
public class RabbitConfig {

    @Resource
    private RabbitMessageService rabbitMessageService;

    @Bean
    public ApplicationRunner runtimeCachePropertiesRunner(CachingConnectionFactory connectionFactory) {
        return args -> {
            Properties cacheProperties = connectionFactory.getCacheProperties();
            log.info("cacheProperties: {}", cacheProperties);
        };
    }

    /**
     * Naming Connections
     *
     * @return ConnectionNameStrategy
     * @see CachingConnectionFactory#setConnectionNameStrategy(ConnectionNameStrategy)
     * @see org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration
     */
    @Bean
    public ConnectionNameStrategy connectionNameStrategy() {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    /**
     * 配置消息转换器
     *
     * @return MessageConverter
     * @see RabbitTemplate#setMessageConverter(MessageConverter)
     * @see org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory#setMessageConverter(MessageConverter)
     */
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 忽略 null 值
        objectMapper.registerModules(new JavaTimeModule()); // 解决 LocalDateTime 的序列化
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplateCustomizer rabbitTemplateCustomizer() {
        return rabbitTemplate -> {
            rabbitTemplate.setUserIdExpressionString("@rabbitConnectionFactory.username");
            // rabbitTemplate.setUsePublisherConnection(true);
            rabbitTemplate.setBeforePublishPostProcessors(message -> {
                MessageProperties properties = message.getMessageProperties();
                properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                properties.setContentEncoding(StandardCharsets.UTF_8.name());
                properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return message;
            });
            // Publisher Confirms and Returns
            // The CorrelationData is an object supplied by the client when sending the original message.
            // The ack is true for an ack and false for a nack.
            // For nack instances, the cause may contain a reason for the nack, if it is available when the nack is generated.
            // An example is when sending a message to a nonexistent exchange. In that case, the broker closes the channel. The reason for the closure is included in the cause.
            // Only one ConfirmCallback is supported by a RabbitTemplate.
            rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                log.info("[消息发送确认] correlationData: {}, ack: {}, cause: {}", correlationData, ack, cause);
                if (ack && Objects.nonNull(correlationData)) {
                    rabbitMessageService.sendSuccess(Long.valueOf(correlationData.getId()));
                }
            });
            // Only one ReturnsCallback is supported by each RabbitTemplate.
            rabbitTemplate.setReturnsCallback(returned -> {
                // 忽略延迟插件返回消息
                if (RabbitConstants.EXCHANGE_DELAY.equals(returned.getExchange())) {
                    return;
                }
                log.info("[消息未正确路由] replyCode: {}, replyText: {}, exchange: {}, routingKey: {}, message: {}",
                        returned.getReplyCode(),
                        returned.getReplyText(),
                        returned.getExchange(),
                        returned.getRoutingKey(),
                        returned.getMessage());
                String messageId = returned.getMessage().getMessageProperties().getCorrelationId();
                if (StrUtil.isNotEmpty(messageId)) {
                    rabbitMessageService.sendReturned(Long.valueOf(messageId));
                }
            });
        };
    }

}
