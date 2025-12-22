package com.xtrarust.cloud.db.mybatis.encrypt.annotation;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;

import java.lang.annotation.*;

/**
 * 字段加密注解
 *
 * @author gova
 */
@Documented
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EncryptField {

    /**
     * 加密算法
     */
    AlgorithmType algorithm() default AlgorithmType.DEFAULT;

    /**
     * 对称加密算法（AES、SM4）秘钥
     */
    String key() default "";

    /**
     * 非对称加密算法（RSA、SM2）公钥
     */
    String publicKey() default "";

    /**
     * 非对称加密算法（RSA、SM2）私钥
     */
    String privateKey() default "";

    /**
     * 编码方式。对加密算法为BASE64的不起作用
     */
    EncodeType encode() default EncodeType.DEFAULT;

}
