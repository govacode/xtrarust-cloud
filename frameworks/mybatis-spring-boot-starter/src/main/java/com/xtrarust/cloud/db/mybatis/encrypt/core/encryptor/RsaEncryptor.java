package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.util.EncryptUtils;

/**
 * RSA算法实现
 *
 * @author gova
 */
public class RsaEncryptor extends AbstractAsymmetricKeyEncryptor {

    public RsaEncryptor(String publicKey, String privateKey) {
        super(publicKey, privateKey);
    }

    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.RSA;
    }

    @Override
    protected String doEncryptHex(String value) {
        return EncryptUtils.encryptByRsaHex(value, this.publicKey);
    }

    @Override
    protected String doEncrypt(String value) {
        return EncryptUtils.encryptByRsa(value, this.publicKey);
    }

    @Override
    protected String doDecrypt(String value) {
        return EncryptUtils.decryptByRsa(value, this.privateKey);
    }
}
