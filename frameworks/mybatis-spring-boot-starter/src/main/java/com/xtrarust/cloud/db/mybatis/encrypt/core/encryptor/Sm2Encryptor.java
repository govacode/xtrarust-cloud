package com.xtrarust.cloud.db.mybatis.encrypt.core.encryptor;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.util.EncryptUtils;

/**
 * sm2算法实现
 *
 * @author gova
 */
public final class Sm2Encryptor extends AbstractAsymmetricKeyEncryptor {

    public Sm2Encryptor(String publicKey, String privateKey) {
        super(publicKey, privateKey);
    }

    @Override
    public AlgorithmType algorithm() {
        return AlgorithmType.SM2;
    }

    @Override
    protected String doEncryptHex(String value) {
        return EncryptUtils.encryptBySm2Hex(value, this.publicKey);
    }

    @Override
    protected String doEncrypt(String value) {
        return EncryptUtils.encryptBySm2(value, this.publicKey);
    }

    @Override
    protected String doDecrypt(String value) {
        return EncryptUtils.decryptBySm2(value, this.privateKey);
    }
}
