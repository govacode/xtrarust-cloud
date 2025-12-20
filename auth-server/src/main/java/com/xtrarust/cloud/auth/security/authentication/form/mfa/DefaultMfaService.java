package com.xtrarust.cloud.auth.security.authentication.form.mfa;

import cn.hutool.core.util.IdUtil;
import com.xtrarust.cloud.auth.common.constant.Constants;
import com.xtrarust.cloud.auth.util.TotpUtil;
import com.xtrarust.cloud.auth.security.userdetails.MfaUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.security.InvalidKeyException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class DefaultMfaService implements MfaService {

    private final RedisOperations<String, Object> redisOperations;

    public DefaultMfaService(RedisOperations<String, Object> redisOperations) {
        this.redisOperations = redisOperations;
    }

    @Override
    public String generateMfaId(Authentication authentication) {
        String mfaId = IdUtil.getSnowflakeNextIdStr();
        redisOperations.opsForValue()
                .setIfAbsent(Constants.CACHE_NAME_MFA_AUTHENTICATION + ":" + mfaId, authentication, new TotpUtil().getTimeStepInSeconds(), TimeUnit.SECONDS);
        return mfaId;
    }

    @Override
    public void sendOtp(String mfaId, MfaUserDetails userDetails) {
        TotpUtil totpUtil = new TotpUtil();
        String key = totpUtil.encodeKey(), otp = null;
        try {
            otp = totpUtil.genOtp(key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        // just mock
        if (userDetails.getSendType() == MfaUserDetails.TotpSendType.SMS) {
            log.info("send otp: {} to {} using sms", otp, userDetails.getDestination());
        } else {
            log.info("send otp: {} to {} using email", otp, userDetails.getDestination());
        }
        redisOperations.opsForValue()
                .setIfAbsent(Constants.CACHE_NAME_TOTP_KEY + ":" + mfaId, key, totpUtil.getTimeStepInSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public Authentication validateOtp(String mfaId, String otp) throws AuthenticationException {
        String key = (String) redisOperations.opsForValue().get(Constants.CACHE_NAME_TOTP_KEY + ":" + mfaId);
        if (key == null) {
            throw new BadCredentialsException("验证码不存在或已过期");
        }
        String generated;
        try {
            generated = new TotpUtil().genOtp(key);
        } catch (InvalidKeyException e) {
            throw new BadCredentialsException("验证码不存在或已过期");
        }
        if (!generated.equals(otp)) {
            throw new BadCredentialsException("验证码不正确");
        }
        Authentication authentication = (Authentication) redisOperations.opsForValue()
                .get(Constants.CACHE_NAME_MFA_AUTHENTICATION + ":" + mfaId);
        if (authentication == null) {
            throw new BadCredentialsException("认证信息已过期");
        }
        return authentication;
    }

}
