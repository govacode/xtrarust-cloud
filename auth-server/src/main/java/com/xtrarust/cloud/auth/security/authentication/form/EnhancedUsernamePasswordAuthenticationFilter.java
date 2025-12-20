package com.xtrarust.cloud.auth.security.authentication.form;

import com.alibaba.fastjson.JSON;
import com.xtrarust.cloud.auth.common.constant.Constants;
import com.xtrarust.cloud.auth.security.authentication.exception.InvalidCaptchaException;
import com.xtrarust.cloud.auth.security.authentication.exception.NeedMfaException;
import com.xtrarust.cloud.auth.security.authentication.form.mfa.MfaService;
import com.xtrarust.cloud.auth.security.userdetails.MfaUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Spring Security {@link UsernamePasswordAuthenticationFilter}增强：
 * 1. 支持json请求
 * 2. 支持图形验证码校验
 * 3. 支持MFA两步验证
 *
 * @author gova
 */
@Setter
@Getter
public class EnhancedUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String CAPTCHA_KEY = "captcha";

    public static final String MFA_ID_KEY = "mfaId";

    public static final String OTP_KEY = "otp";

    // 默认不启用图形验证码
    private boolean captchaEnabled = false;

    private String captchaParameter = CAPTCHA_KEY;

    private String mfaIdParameter = MFA_ID_KEY;

    private String otpParameter = OTP_KEY;

    private MfaService mfaService;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        if (isJsonRequest(request.getContentType())) {
            try (InputStream is = request.getInputStream()) {
                Map<String, String> loginReq = JSON.parseObject(is, Map.class);
                String captchaCode = loginReq.get(getCaptchaParameter());
                String mfaId = loginReq.get(getMfaIdParameter());
                if (!StringUtils.hasText(mfaId)) {
                    if (isCaptchaEnabled()) {
                        checkCaptcha(captchaCode, request);
                    }

                    String username = loginReq.get(getUsernameParameter());
                    String password = loginReq.get(getPasswordParameter());

                    username = username == null ? "" : username.trim();
                    password = password == null ? "" : password;

                    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
                    setDetails(request, authRequest);
                    Authentication authentication = this.getAuthenticationManager().authenticate(authRequest);
                    return mfaIfNecessary(response, authentication);
                } else { // 验证OTP
                    return mfaService.validateOtp(mfaId, loginReq.get(getOtpParameter()));
                }
            } catch (IOException e) {
                throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        } else {
            String mfaId = obtainMfaId(request);
            if (!StringUtils.hasText(mfaId)) {
                if (isCaptchaEnabled()) {
                    checkCaptcha(obtainCaptcha(request), request);
                }
                Authentication authentication = super.attemptAuthentication(request, response);
                return mfaIfNecessary(response, authentication);
            } else {
                return mfaService.validateOtp(mfaId, obtainOtp(request));
            }
        }
    }

    private Authentication mfaIfNecessary(HttpServletResponse response, Authentication authentication) {
        if (authentication.getPrincipal() instanceof MfaUserDetails userDetails) {
            if (userDetails.is2FAEnabled() && mfaService != null) { // 启用两步验证
                // 生成mfaId
                String genMfaId = mfaService.generateMfaId(authentication);
                response.setHeader(Constants.MFA_RESPONSE_HEADER, "mfa,realm=" + genMfaId);
                // 发送OTP
                mfaService.sendOtp(genMfaId, userDetails);
                throw new NeedMfaException("已向" + userDetails.getDestination() + "发送验证码");
            }
        }
        return authentication;
    }

    private void checkCaptcha(String captchaCode, HttpServletRequest request) {
        String validateCode = (String) request.getSession().getAttribute(Constants.SESSION_ATTR_NAME_CAPTCHA);
        if (StringUtils.hasText(validateCode)) {
            // 无论验证成功还是失败 都应清除验证码
            request.getSession().removeAttribute(Constants.SESSION_ATTR_NAME_CAPTCHA);
        }
        if (ObjectUtils.isEmpty(validateCode) || !validateCode.equalsIgnoreCase(captchaCode)) {
            throw new InvalidCaptchaException(messages.getMessage("EnhancedUsernamePasswordAuthenticationFilter.InvalidCaptcha", "Invalid captcha"));
        }
    }

    private boolean isJsonRequest(String contentType) {
        return contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    private String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter(this.captchaParameter);
    }

    private String obtainMfaId(HttpServletRequest request) {
        return request.getParameter(this.mfaIdParameter);
    }

    private String obtainOtp(HttpServletRequest request) {
        return request.getParameter(this.otpParameter);
    }

}
