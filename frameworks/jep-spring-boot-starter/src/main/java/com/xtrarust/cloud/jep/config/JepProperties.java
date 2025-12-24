package com.xtrarust.cloud.jep.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "jep")
public class JepProperties {

    /**
     * 是否开启 JEP 自动配置
     */
    private boolean enabled = false;

    /**
     * Python 模块搜索路径 (sys.path)
     */
    private List<String> includePaths = new ArrayList<>();

    /**
     * Interpreter 对象池
     */
    private Pool pool = new Pool();

    @Data
    public static class Pool {

        /**
         * 对象池最大实例数
         */
        private int maxTotal = Runtime.getRuntime().availableProcessors() * 2;

        /**
         * 对象池最大空闲数
         */
        private int maxIdle = Runtime.getRuntime().availableProcessors();

        /**
         * 借用对象时的最长等待时间
         */
        private Duration maxWait = Duration.ofMillis(5_000);
    }
}
