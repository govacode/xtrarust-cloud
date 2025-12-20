package com.xtrarust.cloud.common.exception;

import com.xtrarust.cloud.common.exception.errorcode.IErrorCode;

/**
 * 业务逻辑异常
 */
public class ServiceException extends BaseException {

    public ServiceException(IErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public ServiceException(IErrorCode errorCode, Object[] args) {
        this(errorCode, null, args);
    }

    public ServiceException(IErrorCode errorCode, Throwable throwable) {
        this(errorCode, throwable, null);
    }

    public ServiceException(IErrorCode errorCode, Throwable throwable, Object[] args) {
        super(errorCode, throwable, args);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + getMessage() + "'" +
                '}';
    }
}
