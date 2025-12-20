package com.xtrarust.cloud.auth.common.constant;

public interface Constants {

    String SESSION_ATTR_NAME_CAPTCHA = "captcha";

    String MFA_RESPONSE_HEADER = "X-Authenticate";

    String CACHE_NAME_MFA_AUTHENTICATION = "mfa_auth";

    String CACHE_NAME_TOTP_KEY = "totp_key";

    String AUTHORITIES = "authorities";
}
