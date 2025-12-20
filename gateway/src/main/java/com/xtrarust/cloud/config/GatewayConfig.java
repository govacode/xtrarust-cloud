package com.xtrarust.cloud.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtrarust.cloud.filter.StripPrefixGlobalFilter;
import com.xtrarust.cloud.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 *
 * @author gova
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfig {

	@Bean
	public StripPrefixGlobalFilter stripPrefixGlobalFilter() {
		return new StripPrefixGlobalFilter();
	}

	/**
	 * 创建全局异常处理程序
	 * @param objectMapper 对象映射器
	 * @return 全局异常处理程序
	 */
	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalExceptionHandler(objectMapper);
	}

}
