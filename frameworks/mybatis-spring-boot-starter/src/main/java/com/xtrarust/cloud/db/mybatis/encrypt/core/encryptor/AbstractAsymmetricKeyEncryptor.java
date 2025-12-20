package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import cn.hutool.core.lang.Assert;
import com.xtrarust.cloud.db.mybatis.encrypt.core.Encryptor;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;

public abstract class AbstractAsymmetricKeyEncryptor implements Encryptor {

    protected String publicKey;

    protected String privateKey;

    protected AbstractAsymmetricKeyEncryptor(String publicKey, String privateKey) {
        Assert.notEmpty(publicKey, "Public key is required.");
        Assert.notEmpty(privateKey, "Private key is required.");
        this.publicKey = publicKey;
        this.privateKey = privateKey;
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
