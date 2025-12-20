package com.xtrarust.cloud.oss.exception;

import java.io.Serial;

/**
 * OSS 异常类
 *
 * @author gova
 */
public class OssException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }

    public OssException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
