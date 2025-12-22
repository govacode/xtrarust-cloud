package com.xtrarust.cloud.sms.config;

import com.xtrarust.cloud.redis.config.RedisAutoConfiguration;
import com.xtrarust.cloud.sms.dao.RedisSmsDao;
import com.xtrarust.cloud.sms.handler.SmsExceptionHandler;
import org.dromara.sms4j.api.dao.SmsDao;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 短信配置类
 *
 * @author gova
 */
@AutoConfiguration(after = {RedisAutoConfiguration.class})
public class SmsAutoConfiguration {

    @Primary
    @Bean
    @ConditionalOnMissingBean
    public SmsDao smsDao(RedisTemplate<String, Object> redisTemplate) {
        return new RedisSmsDao(redisTemplate);
    }

    /**
     * 异常处理器
     */
    @Bean
    public SmsExceptionHandler smsExceptionHandler() {
        return new SmsExceptionHandler();
    }

}
