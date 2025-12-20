package com.xtrarust.cloud.job.config;

import com.xxl.job.core.executor.XxlJobExecutor;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * XXL-Job 自动配置类<br>
 * <a href="https://www.xuxueli.com/xxl-job/">XXL-JOB分布式任务调度平台</a>
 *
 * @author gova
 */
@AutoConfiguration
@ConditionalOnClass(XxlJobSpringExecutor.class)
@EnableConfigurationProperties({XxlJobProperties.class})
@EnableScheduling // 开启 Spring 自带的定时任务
@Slf4j
public class XxlJobAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "xxl.job", name = "enabled", havingValue = "true")
    public XxlJobExecutor xxlJobExecutor(XxlJobProperties properties) {
        log.info(">>>>>> 初始化 XXL-Job 执行器");
        XxlJobProperties.AdminProperties admin = properties.getAdmin();
        XxlJobProperties.ExecutorProperties executor = properties.getExecutor();

        // 初始化执行器
        XxlJobExecutor xxlJobExecutor = new XxlJobSpringExecutor();
        xxlJobExecutor.setIp(executor.getIp());
        xxlJobExecutor.setPort(executor.getPort());
        xxlJobExecutor.setAppname(executor.getAppName());
        xxlJobExecutor.setLogPath(executor.getLogPath());
        xxlJobExecutor.setLogRetentionDays(executor.getLogRetentionDays());
        xxlJobExecutor.setAdminAddresses(admin.getAddresses());
        xxlJobExecutor.setAccessToken(properties.getAccessToken());
        return xxlJobExecutor;
    }

}
