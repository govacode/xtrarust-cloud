package com.xtrarust.cloud.auth.security.userdetails;

import org.springframework.security.core.userdetails.UserDetails;

public interface MfaUserDetails extends UserDetails {

    /**
     * 是否启用两步验证
     *
     * @return true if the user enables Two-factor authentication (2FA), false otherwise
     */
    boolean is2FAEnabled();

    /**
     * 获取TOTP发送方式
     *
     * @return the sendType
     */
    TotpSendType getSendType();

    /**
     * 获取TOTP发送目的地
     *
     * @return the destination
     */
    String getDestination();

    enum TotpSendType {

        SMS,
        EMAIL;
    }
}
