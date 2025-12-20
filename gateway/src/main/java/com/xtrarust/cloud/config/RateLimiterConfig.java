package com.xtrarust.cloud.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 路由限流配置类
 *
 * @author gova
 * @link <a href="https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#the-requestratelimiter-gatewayfilter-factory">Spring Cloud Gateway文档</a>
 */
@Configuration(proxyBeanMethods = false)
public class RateLimiterConfig {

	/**
	 * 创建基于IP的KeyResolver实例
	 */
	@Bean
	public KeyResolver remoteAddrKeyResolver() {
		return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
	}

}
