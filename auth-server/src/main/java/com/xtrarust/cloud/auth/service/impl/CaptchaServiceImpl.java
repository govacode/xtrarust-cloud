package com.xtrarust.cloud.auth.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.IdUtil;
import com.xtrarust.cloud.auth.config.properties.AuthServerSecurityProperties;
import com.xtrarust.cloud.auth.domain.response.CaptchaResp;
import com.xtrarust.cloud.auth.service.CaptchaService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 图形验证码 Service 实现
 *
 * @author gova
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private AuthServerSecurityProperties securityProperties;

    @Override
    public Captcha getSystemCaptchaImage(HttpSession session) {
        // https://gitee.com/ele-admin/EasyCaptcha
        // 生成验证码
        Captcha captcha = createCaptcha();
        // 验证码存入session
        session.setAttribute("captcha", captcha.text());
        return captcha;
    }

    private Captcha createCaptcha() {
        int width = securityProperties.getCaptcha().getWidth(), height = securityProperties.getCaptcha().getHeight();
        SpecCaptcha captcha = new SpecCaptcha(width, height, 4);
        captcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        if (log.isDebugEnabled()) {
            log.debug("captcha created, captcha code: {}", captcha.text());
        }
        return captcha;
    }

    @Override
    public CaptchaResp getCaptchaImage(HttpSession session) {
        if (BooleanUtil.isFalse(securityProperties.getCaptcha().getEnable())) {
            return CaptchaResp.builder().enable(false).build();
        }
        // 生成验证码
        // CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(securityProperties.getCaptcha().getWidth(), securityProperties.getCaptcha().getHeight());
        Captcha captcha = createCaptcha();
        // 缓存到 Redis 中
        String uuid = IdUtil.fastSimpleUUID();
        // stringRedisTemplate.opsForValue().set(CAPTCHA_KEY_PREFIX + uuid, captcha.text(), securityProperties.getCaptcha().getTimeout().getSeconds(), TimeUnit.SECONDS);
        // 验证码存入session
        session.setAttribute("captcha", captcha.text());
        return CaptchaResp.builder().enable(true).uuid(uuid).img(captcha.toBase64()).build();
    }

}
