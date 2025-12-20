package com.xtrarust.cloud.auth.security.userdetails;

import com.xtrarust.cloud.auth.security.authentication.sms.MobileSmsCodeAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Core interface which loads user-specific data by mobile.
 * <p>
 * It is used throughout the framework as a user DAO and is the strategy used by the
 * {@link MobileSmsCodeAuthenticationProvider}.
 *
 * <p>
 * The interface requires only one read-only method, which simplifies support for new
 * data-access strategies.
 *
 * @author gova
 * @see MobileSmsCodeAuthenticationProvider
 * @see UserDetails
 */
@FunctionalInterface
public interface MobileUserDetailsService {

    UserDetails loadUserByMobile(String mobile) throws MobileNotFoundException;
}
