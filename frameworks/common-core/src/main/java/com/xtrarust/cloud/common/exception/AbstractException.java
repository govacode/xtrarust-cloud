package com.xtrarust.cloud.common.exception;

import com.xtrarust.cloud.common.exception.errorcode.IErrorCode;
import com.xtrarust.cloud.common.util.MessageUtils;
import com.xtrarust.cloud.common.util.string.StringUtils;
import lombok.Getter;

/**
 * 抽象项目中三类异常体系，客户端异常、服务端异常以及远程服务调用异常
 *
 * @see ClientException
 * @see ServiceException
 * @see RemoteException
 */
@Getter
public abstract class AbstractException extends RuntimeException {

    public final String errorCode;

    private final Object[] args;

    public AbstractException(IErrorCode errorCode, Throwable throwable, Object[] args) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode.getCode();
        this.args = args;
    }

    @Override
    public String getMessage() {
        String message = null;
        if (StringUtils.isNotEmpty(errorCode)) {
            message = MessageUtils.message(errorCode, args);
        }
        if (message == null) {
            message = super.getMessage();
        }
        return message;
    }
}
