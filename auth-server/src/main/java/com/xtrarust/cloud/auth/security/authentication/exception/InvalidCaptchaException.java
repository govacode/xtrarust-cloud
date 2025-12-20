package com.xtrarust.cloud.auth.security.authentication.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidCaptchaException extends AuthenticationException {

    public InvalidCaptchaException(String msg) {
        super(msg);
    }

    public InvalidCaptchaException(String msg, Throwable t) {
        super(msg, t);
    }
}
