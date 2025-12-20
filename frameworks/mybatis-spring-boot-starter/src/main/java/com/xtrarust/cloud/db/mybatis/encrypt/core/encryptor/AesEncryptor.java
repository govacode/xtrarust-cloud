package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.util.EncryptUtils;

/**
 * AES算法实现
 *
 * @author gova
 */
public final class AesEncryptor extends AbstractSymmetricKeyEncryptor {

    public AesEncryptor(String key) {
        super(key);
    }

    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.AES;
    }

    @Override
    protected String doEncryptHex(String value) {
        return EncryptUtils.encryptByAesHex(value, this.key);
    }

    @Override
    protected String doEncrypt(String value) {
        return EncryptUtils.encryptByAes(value, this.key);
    }

    @Override
    protected String doDecrypt(String value) {
        return EncryptUtils.decryptByAes(value, this.key);
    }
}
