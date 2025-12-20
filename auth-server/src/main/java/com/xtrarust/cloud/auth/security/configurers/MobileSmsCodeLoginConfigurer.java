package com.xtrarust.cloud.auth.security.configurers;

import com.xtrarust.cloud.auth.security.authentication.sms.MobileSmsCodeAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class MobileSmsCodeLoginConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractLoginFilterConfigurer<H, MobileSmsCodeLoginConfigurer<H>, MobileSmsCodeAuthenticationFilter> {

    public MobileSmsCodeLoginConfigurer() {
        super(new MobileSmsCodeAuthenticationFilter(), "/login/sms");
    }

    public MobileSmsCodeLoginConfigurer<H> mobileParameter(String mobileParameter) {
        getAuthenticationFilter().setMobileParameter(mobileParameter);
        return this;
    }

    public MobileSmsCodeLoginConfigurer<H> smsCodeParameter(String smsCodeParameter) {
        getAuthenticationFilter().setSmsCodeParameter(smsCodeParameter);
        return this;
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
    }
}
