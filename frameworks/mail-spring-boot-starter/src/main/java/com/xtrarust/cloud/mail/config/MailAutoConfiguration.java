package com.xtrarust.cloud.mail.config;

import cn.hutool.extra.mail.MailAccount;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * JavaMail 配置
 *
 * @author gova
 */
@AutoConfiguration
@EnableConfigurationProperties(MailProperties.class)
public class MailAutoConfiguration {

    @Bean
    @ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
    public MailAccount mailAccount(MailProperties mailProperties) {
        MailAccount account = new MailAccount();
        account.setHost(mailProperties.getHost());
        account.setPort(mailProperties.getPort());
        account.setAuth(mailProperties.getAuth());
        account.setFrom(mailProperties.getFrom());
        account.setUser(mailProperties.getUsername());
        account.setPass(mailProperties.getPassword());
        account.setSocketFactoryPort(mailProperties.getPort());
        account.setStarttlsEnable(mailProperties.getStarttlsEnable());
        account.setSslEnable(mailProperties.getSslEnable());
        if (mailProperties.getTimeout() != null) {
            account.setTimeout(mailProperties.getTimeout().toMillis());
        }
        if (mailProperties.getConnectionTimeout() != null) {
            account.setConnectionTimeout(mailProperties.getConnectionTimeout().toMillis());
        }
        return account;
    }

}
