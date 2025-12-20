package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.util.EncryptUtils;

/**
 * sm4算法实现
 *
 * @author gova
 */
public final class Sm4Encryptor extends AbstractSymmetricKeyEncryptor {

    public Sm4Encryptor(String key) {
        super(key);
    }

    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.SM4;
    }

    @Override
    protected String doEncryptHex(String value) {
        return EncryptUtils.encryptBySm4Hex(value, this.key);
    }

    @Override
    protected String doEncrypt(String value) {
        return EncryptUtils.encryptBySm4(value, this.key);
    }

    @Override
    protected String doDecrypt(String value) {
        return EncryptUtils.decryptBySm4(value, this.key);
    }
}
