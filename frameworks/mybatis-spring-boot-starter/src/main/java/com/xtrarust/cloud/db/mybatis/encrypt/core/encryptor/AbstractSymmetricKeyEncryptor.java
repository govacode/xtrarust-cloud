package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import com.xtrarust.cloud.db.mybatis.encrypt.core.Encryptor;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;

public abstract class AbstractSymmetricKeyEncryptor implements Encryptor {

    protected String key;

    protected AbstractSymmetricKeyEncryptor(String key) {
        this.key = key;
    }

    @Override
    public String encrypt(String value, EncodeType encodeType) {
        if (encodeType == EncodeType.HEX) {
            return doEncryptHex(value);
        }
        return doEncrypt(value);
    }

    @Override
    public String decrypt(String value) {
        return doDecrypt(value);
    }

    protected abstract String doEncryptHex(String value);

    protected abstract String doEncrypt(String value);

    protected abstract String doDecrypt(String value);
}
