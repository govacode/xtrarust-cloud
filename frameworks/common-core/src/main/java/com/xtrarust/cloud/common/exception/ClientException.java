package com.xtrarust.cloud.common.exception;

import com.xtrarust.cloud.common.exception.errorcode.IErrorCode;

/**
 * 客户端异常
 */
public class ClientException extends AbstractException {

    public ClientException(IErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public ClientException(IErrorCode errorCode, Object[] args) {
        this(errorCode, null, args);
    }

    public ClientException(IErrorCode errorCode, Throwable throwable) {
        this(errorCode, throwable, null);
    }

    public ClientException(IErrorCode errorCode, Throwable throwable, Object[] args) {
        super(errorCode, throwable, args);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + getMessage() + "'" +
                '}';
    }
}
