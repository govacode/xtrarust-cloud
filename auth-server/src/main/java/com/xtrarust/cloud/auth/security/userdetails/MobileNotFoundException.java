package com.xtrarust.cloud.auth.security.userdetails;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;

/**
 * Thrown if an {@link MobileUserDetailsService} implementation cannot locate a {@link User} by
 * its mobile.
 *
 * @author gova
 */
public class MobileNotFoundException extends AuthenticationException {

    public MobileNotFoundException(String msg) {
        super(msg);
    }

    public MobileNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
