package com.xtrarust.cloud.auth.web;

import com.xtrarust.cloud.auth.domain.response.CaptchaResp;
import com.xtrarust.cloud.auth.domain.request.SmsCodeReq;
import com.xtrarust.cloud.auth.security.authentication.sms.SmsCodeService;
import com.xtrarust.cloud.auth.service.CaptchaService;
import com.wf.captcha.base.Captcha;
import com.xtrarust.cloud.common.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@Controller
@Tag(name = "验证码")
@RequestMapping("/code")
@RequiredArgsConstructor
public class VerificationCodeController {

    private final CaptchaService captchaService;

    private final SmsCodeService smsCodeService;

    @GetMapping("/captcha")
    @Operation(summary = "获取图形验证码")
    public void getCaptchaImage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setHeader("Cache-Control", "no-store, no-cache");
        Captcha captcha = captchaService.getSystemCaptchaImage(req.getSession());
        captcha.out(resp.getOutputStream());
    }

    @GetMapping("/image")
    @ResponseBody
    @Operation(summary = "获取图形验证码-Base64")
    public R<CaptchaResp> getCaptchaImage(HttpSession session) {
        return R.ok(captchaService.getCaptchaImage(session));
    }

    @PostMapping("/sms")
    @ResponseBody
    @Operation(summary = "获取短信验证码")
    public R<Boolean> sendSmsCode(@Valid @RequestBody SmsCodeReq reqVO) {
        smsCodeService.sendSmsCode(reqVO.getMobile());
        return R.ok(true);
    }
}
