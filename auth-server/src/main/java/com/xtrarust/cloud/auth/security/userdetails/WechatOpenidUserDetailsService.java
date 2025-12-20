package com.xtrarust.cloud.auth.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@FunctionalInterface
public interface WechatOpenidUserDetailsService {

    UserDetails loadUserByOpenid(String openid) throws UsernameNotFoundException;
}
