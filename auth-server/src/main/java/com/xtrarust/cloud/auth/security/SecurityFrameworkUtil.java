package com.xtrarust.cloud.auth.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public final class SecurityFrameworkUtil {

    public static Object getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return principal;
        }
        if (principal instanceof OAuth2User) {
            return principal;
        }
        return null;
    }
}
