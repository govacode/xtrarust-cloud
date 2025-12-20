package com.xtrarust.cloud.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    // AOP 切面
    // DynamicDataSourceAnnotationAdvisor 处理DS注解
    // 前置通知 DynamicDataSourceAnnotationInterceptor 实际就是将解析的数据源名称放入ThreadLocal中 DynamicDataSourceContextHolder
    // DsProcessor 责任链 决定具体使用哪个数据源 依次从请求头、session、spel中获取数据源名称
    //
    // DynamicRoutingDataSource
    // DataSourceProvider Map<String, DataSource> loadDataSources -> DefaultDataSourceCreator#createDataSource
    //
    // 
}
