package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import com.xtrarust.cloud.db.mybatis.encrypt.core.Encryptor;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;
import com.xtrarust.cloud.db.mybatis.encrypt.util.EncryptUtils;

/**
 * Base64算法实现
 *
 * @author gova
 */
public final class Base64Encryptor implements Encryptor {

    public Base64Encryptor() {
    }

    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.BASE64;
    }

    @Override
    public String encrypt(String value, EncodeType encodeType) {
        return EncryptUtils.encryptByBase64(value);
    }

    @Override
    public String decrypt(String value) {
        return EncryptUtils.decryptByBase64(value);
    }
}
