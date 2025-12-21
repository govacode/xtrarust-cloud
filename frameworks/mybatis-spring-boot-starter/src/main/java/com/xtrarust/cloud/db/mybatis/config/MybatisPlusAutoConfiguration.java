package com.xtrarust.cloud.db.mybatis.config;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.xtrarust.cloud.db.mybatis.core.handler.AuditFieldMetaObjectHandler;
import com.xtrarust.cloud.db.mybatis.core.handler.SnowflakeIdGenerator;
import com.xtrarust.cloud.db.mybatis.encrypt.core.EncryptorManager;
import com.xtrarust.cloud.db.mybatis.encrypt.interceptor.MybatisDecryptInterceptor;
import com.xtrarust.cloud.db.mybatis.encrypt.interceptor.MybatisEncryptInterceptor;
import com.xtrarust.cloud.db.mybatis.encrypt.properties.EncryptorProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * MyBaits 配置类 <a href="https://baomidou.com/">MyBatis Plus</a>
 */
@AutoConfiguration
@MapperScan(value = "${mybatis-plus.mapper.base-package}")
public class MybatisPlusAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件 https://baomidou.com/pages/97710a/
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        // 乐观锁插件 https://baomidou.com/pages/0d93c0/#optimisticlockerinnerinterceptor
        // 使用：在实体类的字段上加上@Version注解
        // 说明：
        // 1. 支持的数据类型只有:int,Integer,long,Long,Date,Timestamp,LocalDateTime
        // 2. 整数类型下 newVersion = oldVersion + 1
        // 3. newVersion 会回写到 entity 中
        // 4. 仅支持 updateById(id) 与 update(entity, wrapper) 方法
        // 5. 在 update(entity, wrapper) 方法下, wrapper 不能复用!!!
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        return interceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler(){
        // 审计字段自动填充
        return new AuditFieldMetaObjectHandler();
    }

    /**
     * 自定义雪花算法 ID生成器
     * @see com.baomidou.mybatisplus.annotation.IdType#ASSIGN_ID
     */
    @Bean
    @Primary
    public IdentifierGenerator idGenerator() {
        return new SnowflakeIdGenerator();
    }

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties({EncryptorProperties.class, MybatisPlusProperties.class})
    @ConditionalOnProperty(value = "mybatis-plus.encryptor.enable", havingValue = "true")
    static class encryptConfig {

        @Bean
        public EncryptorManager encryptorManager(MybatisPlusProperties mybatisPlusProperties) {
            return new EncryptorManager(mybatisPlusProperties.getTypeAliasesPackage());
        }

        @Bean
        public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager,
                                                                   EncryptorProperties  properties) {
            return new MybatisEncryptInterceptor(encryptorManager, properties);
        }

        @Bean
        public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager,
                                                                   EncryptorProperties  properties) {
            return new MybatisDecryptInterceptor(encryptorManager, properties);
        }
    }
}
