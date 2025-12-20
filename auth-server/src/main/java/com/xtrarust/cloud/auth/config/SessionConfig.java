package com.xtrarust.cloud.auth.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.xtrarust.cloud.auth.security.authentication.exception.InvalidCaptchaException;
import com.xtrarust.cloud.auth.security.authentication.exception.NeedMfaException;
import com.xtrarust.cloud.auth.security.authentication.sms.MobileSmsCodeAuthenticationToken;
import com.xtrarust.cloud.auth.security.jackson2.InvalidCaptchaExceptionMixin;
import com.xtrarust.cloud.auth.security.jackson2.MobileSmsCodeAuthenticationTokenMixin;
import com.xtrarust.cloud.auth.security.jackson2.NeedMfaExceptionMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;

import java.util.List;

/**
 * Spring Security集成Spring session配置
 * <a href="https://docs.spring.io/spring-security/reference/features/integrations/jackson.html"></a>
 *
 * @author gova
 */
@Configuration
public class SessionConfig {

    private ClassLoader classLoader;

    @Bean
    public RedisSerializer<?> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    /**
     * Customized {@link ObjectMapper} to add mix-in for class that doesn't have default
     * constructors
     * @return the {@link ObjectMapper} to use
     */
    public static ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        List<Module> modules = SecurityJackson2Modules.getModules(SessionConfig.class.getClassLoader());
        modules.add(new SimpleModule() {
            @Override
            public void setupModule(SetupContext context) {
                context.setMixInAnnotations(MobileSmsCodeAuthenticationToken.class, MobileSmsCodeAuthenticationTokenMixin.class);
                context.setMixInAnnotations(InvalidCaptchaException.class, InvalidCaptchaExceptionMixin.class);
                context.setMixInAnnotations(NeedMfaException.class, NeedMfaExceptionMixin.class);
                // https://github.com/spring-projects/spring-security/issues/12294
                // 截止 2023-10-26 此ISSUE处于open状态 https://github.com/spring-projects/spring-session/issues/2305
                mapper.addMixIn(Long.class, LongMixin.class);
            }
        });
        mapper.registerModules(modules);
        mapper.registerModule(new CoreJackson2Module());
        return mapper;
    }

    abstract static class LongMixin {
        @SuppressWarnings("unused")
        @JsonProperty("long")
        Long value;
    }
}
