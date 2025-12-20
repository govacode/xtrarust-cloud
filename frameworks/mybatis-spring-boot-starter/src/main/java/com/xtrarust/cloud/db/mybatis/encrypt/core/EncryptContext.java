package com.xtrarust.cloud.db.mybatis.encrypt.core;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;
import lombok.Data;

/**
 * 加密上下文 用于encryptor传递必要的参数
 *
 * @author gova
 */
@Data
public class EncryptContext {

    /**
     * 默认算法
     */
    private AlgorithmType algorithm;

    /**
     * 对称加密算法秘钥
     */
    private String key;

    /**
     * 非对称加密算法公钥
     */
    private String publicKey;

    /**
     * 非对称加密算法私钥
     */
    private String privateKey;

    /**
     * 编码方式，base64/hex
     */
    private EncodeType encode;

}
