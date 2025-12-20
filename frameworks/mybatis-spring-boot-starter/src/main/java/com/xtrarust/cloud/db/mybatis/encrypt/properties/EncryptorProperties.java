package com.xtrarust.cloud.db.mybatis.encrypt.properties;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加解密属性配置类
 *
 * @author gova
 */
@Data
@ConfigurationProperties(prefix = "mybatis-plus.encryptor")
public class EncryptorProperties {

    /**
     * 过滤开关
     */
    private Boolean enable;

    /**
     * 默认算法
     */
    private AlgorithmType algorithm;

    /**
     * 安全秘钥
     */
    private String key;

    /**
     * 公钥
     */
    private String publicKey;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;

}
