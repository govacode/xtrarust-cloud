package com.xtrarust.cloud.db.mybatis.encrypt.enums;

import com.xtrarust.cloud.db.mybatis.encrypt.core.Encryptor;
import com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 算法名称
 *
 * @author gova
 */
@Getter
@AllArgsConstructor
public enum AlgorithmType {

    /**
     * 默认走yml配置
     */
    DEFAULT(null),
    BASE64(Base64Encryptor.class),
    AES(AesEncryptor.class),
    RSA(RsaEncryptor.class),
    SM2(Sm2Encryptor.class),
    SM4(Sm4Encryptor.class);

    private final Class<? extends Encryptor> clazz;
}
