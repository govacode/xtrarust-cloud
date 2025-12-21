package com.xtrarust.cloud.web.web.config;

import org.hibernate.validator.HibernateValidatorConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * 配置 HibernateValidator 快速失败
 *
 * @author gova
 */
@AutoConfiguration
public class FailFastValidatorConfiguration {

    @Bean
    public ValidationConfigurationCustomizer hibernateValidatorCustomizer() {
        return configuration -> {
            if (configuration instanceof HibernateValidatorConfiguration) {
                // addProperty(BaseHibernateValidatorConfiguration.FAIL_FAST, "true");
                // 设置快速失败模式，即校验过程中一旦遇到失败，立即停止并返回错误
                ((HibernateValidatorConfiguration) configuration).failFast(true);
            }
        };
    }
}
