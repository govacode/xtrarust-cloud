package com.xtrarust.cloud.jep.config;

import com.xtrarust.cloud.jep.core.JepPooledObjectFactory;
import com.xtrarust.cloud.jep.core.JepTemplate;
import jep.Interpreter;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(value = "jep.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JepProperties.class)
public class JepAutoConfiguration {

    @Bean
    public JepPooledObjectFactory  jepPooledObjectFactory(JepProperties properties) {
        return new JepPooledObjectFactory(properties);
    }

    @Bean
    public GenericObjectPool<Interpreter> jepPool(JepPooledObjectFactory factory, JepProperties properties) {
        GenericObjectPoolConfig<Interpreter> config = new GenericObjectPoolConfig<>();
        JepProperties.Pool pool = properties.getPool();
        config.setMaxTotal(pool.getMaxTotal());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMaxWait(pool.getMaxWait());
        config.setTestOnBorrow(true);
        return new GenericObjectPool<>(factory, config);
    }

    @Bean
    public JepTemplate jepTemplate(GenericObjectPool<Interpreter> pool) {
        return new JepTemplate(pool);
    }
}
