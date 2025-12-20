package com.xtrarust.cloud.auth.service;

import com.xtrarust.cloud.auth.domain.response.CaptchaResp;
import com.wf.captcha.base.Captcha;
import jakarta.servlet.http.HttpSession;

/**
 * 图形验证码 Service 接口
 *
 * @author gova
 */
public interface CaptchaService {

    /**
     * 获得系统后台验证码图片
     *
     * @param session session
     * @return 验证码图片
     */
    Captcha getSystemCaptchaImage(HttpSession session);

    /**
     * 获得验证码图片
     *
     * @return 验证码图片
     */
    CaptchaResp getCaptchaImage(HttpSession session);
}
