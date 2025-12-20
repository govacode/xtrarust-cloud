package com.xtrarust.cloud.common.exception;

import com.xtrarust.cloud.common.exception.errorcode.IErrorCode;

/**
 * 远程服务调用异常
 */
public class RemoteException extends AbstractException {

    public RemoteException(IErrorCode errorCode) {
        this(errorCode, null, null);
    }

    public RemoteException(IErrorCode errorCode, Object[] args) {
        this(errorCode, null, args);
    }

    public RemoteException(IErrorCode errorCode, Throwable throwable) {
        this(errorCode, throwable, null);
    }

    public RemoteException(IErrorCode errorCode, Throwable throwable, Object[] args) {
        super(errorCode, throwable, args);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + getMessage() + "'" +
                '}';
    }
}
