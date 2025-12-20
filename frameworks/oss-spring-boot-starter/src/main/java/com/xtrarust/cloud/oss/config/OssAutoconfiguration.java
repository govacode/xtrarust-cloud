package com.xtrarust.cloud.oss.config;

import com.xtrarust.cloud.oss.client.OssClient;
import com.xtrarust.cloud.oss.properties.OssProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoconfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OssClient ossClient(OssProperties ossProperties) {
        return new OssClient(ossProperties);
    }
}
