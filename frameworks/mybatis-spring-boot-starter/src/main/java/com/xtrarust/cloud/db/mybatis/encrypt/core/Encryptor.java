package com.xtrarust.cloud.db.mybatis.encrypt.core;

import com.xtrarust.cloud.db.mybatis.encrypt.enums.AlgorithmType;
import com.xtrarust.cloud.db.mybatis.encrypt.enums.EncodeType;

/**
 * 加解者
 *
 * @author gova
 */
public interface Encryptor {

    /**
     * 当前加密算法
     */
    AlgorithmType algorithm();

    /**
     * 加密
     *
     * @param value      待加密字符串
     * @param encodeType 加密后的编码格式
     * @return 加密后的字符串
     */
    String encrypt(String value, EncodeType encodeType);

    /**
     * 解密
     *
     * @param value 待解密字符串
     * @return 解密后的字符串
     */
    String decrypt(String value);
}
