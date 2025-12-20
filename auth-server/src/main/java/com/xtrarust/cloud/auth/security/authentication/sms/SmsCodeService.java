package com.xtrarust.cloud.auth.security.authentication.sms;

/**
 * 短信验证码 Service
 *
 * @author gova
 */
public interface SmsCodeService {

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    void sendSmsCode(String mobile);

    /**
     * 验证短信验证码
     *
     * @param mobile 手机号
     * @param smsCode 短信验证码
     * @return 验证码是否正确
     */
    boolean verifySmsCode(String mobile, String smsCode);
}
