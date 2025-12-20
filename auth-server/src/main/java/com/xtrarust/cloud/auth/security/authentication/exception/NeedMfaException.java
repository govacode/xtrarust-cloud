package com.xtrarust.cloud.auth.security.authentication.exception;

import org.springframework.security.core.AuthenticationException;

public class NeedMfaException extends AuthenticationException {

    public NeedMfaException(String msg) {
        super(msg);
    }

    public NeedMfaException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
