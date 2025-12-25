package com.xtrarust.cloud.jep.config;

import com.xtrarust.cloud.jep.core.JepTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(value = "jep.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JepProperties.class)
public class JepAutoConfiguration {

    @Bean
    public JepTemplate jepTemplate(JepProperties jepProperties) {
        return new JepTemplate(jepProperties);
    }
}
