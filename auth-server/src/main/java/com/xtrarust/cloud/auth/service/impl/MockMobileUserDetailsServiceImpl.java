package com.xtrarust.cloud.auth.service.impl;

import com.xtrarust.cloud.auth.security.userdetails.MobileNotFoundException;
import com.xtrarust.cloud.auth.security.userdetails.MobileUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MockMobileUserDetailsServiceImpl implements MobileUserDetailsService {

    @Override
    public UserDetails loadUserByMobile(String mobile) throws MobileNotFoundException {
        return User.builder().username(mobile).password("{noop}123456").roles("ADMIN").build();
    }
}
