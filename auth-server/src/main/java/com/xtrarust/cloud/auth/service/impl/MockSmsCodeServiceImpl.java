package com.xtrarust.cloud.auth.service.impl;

import com.xtrarust.cloud.auth.security.authentication.sms.SmsCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockSmsCodeServiceImpl implements SmsCodeService {

    private static final String SMS_CODE_KEY = "auth:sms:";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendSmsCode(String mobile) {
        String smsCode = RandomStringUtils.secure().nextNumeric(6);
        stringRedisTemplate.opsForValue().set(SMS_CODE_KEY + mobile, smsCode, 60, TimeUnit.SECONDS);
        log.info("send sms code: {} to mobile: {}", smsCode, mobile);
        // TODO 真实短信发送
    }

    @Override
    public boolean verifySmsCode(String mobile, String smsCode) {
        String smsCodeInCache = stringRedisTemplate.opsForValue().get(SMS_CODE_KEY + mobile);
        return StringUtils.isNotEmpty(smsCodeInCache) && smsCodeInCache.equals(smsCode);
    }
}
