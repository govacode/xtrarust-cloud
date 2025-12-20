package com.xtrarust.cloud.auth.security.authentication.sms;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class MobileSmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String MOBILE_KEY = "mobile";

    public static final String SMS_CODE_KEY = "smsCode";

    private String mobileParameter = MOBILE_KEY;

    private String smsCodeParameter = SMS_CODE_KEY;

    private boolean postOnly = true;

    public MobileSmsCodeAuthenticationFilter() {
        super(PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, "/login/sms"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String mobile, smsCode;
        if (isJsonRequest(request.getContentType())) {
            try (InputStream is = request.getInputStream()) {
                Map<String, String> loginReq = JSON.parseObject(is, Map.class);
                mobile = loginReq.get(getMobileParameter());
                smsCode = loginReq.get(getSmsCodeParameter());
            } catch (IOException e) {
                throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
        } else {
            mobile = obtainMobile(request);
            smsCode = obtainSmsCode(request);
        }

        mobile = mobile == null ? "" : mobile.trim();
        smsCode = smsCode == null ? "" : smsCode;

        MobileSmsCodeAuthenticationToken authRequest = new MobileSmsCodeAuthenticationToken(mobile, smsCode);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private boolean isJsonRequest(String contentType) {
        return contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE);
    }

    private String obtainMobile(HttpServletRequest request) {
        return request.getParameter(mobileParameter);
    }

    private String obtainSmsCode(HttpServletRequest request) {
        return request.getParameter(smsCodeParameter);
    }

    protected void setDetails(HttpServletRequest request,
                              MobileSmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

}
