package com.xtrarust.cloud.auth.security.configurers;

import com.xtrarust.cloud.auth.security.authentication.form.EnhancedUsernamePasswordAuthenticationFilter;
import com.xtrarust.cloud.auth.security.authentication.form.mfa.MfaService;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.authentication.ForwardAuthenticationFailureHandler;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class UsernamePasswordLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, UsernamePasswordLoginConfigurer<H>, EnhancedUsernamePasswordAuthenticationFilter> {

    public UsernamePasswordLoginConfigurer() {
        super(new EnhancedUsernamePasswordAuthenticationFilter(), null);
    }

    @Override
    public UsernamePasswordLoginConfigurer<H> loginPage(String loginPage) {
        return super.loginPage(loginPage);
    }

    public UsernamePasswordLoginConfigurer<H> captchaEnabled(boolean captchaEnabled) {
        getAuthenticationFilter().setCaptchaEnabled(captchaEnabled);
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> usernameParameter(String usernameParameter) {
        getAuthenticationFilter().setUsernameParameter(usernameParameter);
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> passwordParameter(String passwordParameter) {
        getAuthenticationFilter().setPasswordParameter(passwordParameter);
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> captchaParameter(String captchaParameter) {
        getAuthenticationFilter().setCaptchaParameter(captchaParameter);
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> mfaIdParameter(String mfaIdParameter) {
        getAuthenticationFilter().setMfaIdParameter(mfaIdParameter);
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> otpParameter(String otpParameter) {
        getAuthenticationFilter().setOtpParameter(otpParameter);
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> failureForwardUrl(String forwardUrl) {
        failureHandler(new ForwardAuthenticationFailureHandler(forwardUrl));
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> successForwardUrl(String forwardUrl) {
        successHandler(new ForwardAuthenticationSuccessHandler(forwardUrl));
        return this;
    }

    public UsernamePasswordLoginConfigurer<H> mfaService(MfaService mfaService) {
        getAuthenticationFilter().setMfaService(mfaService);
        return this;
    }

    @Override
    public void init(H http) throws Exception {
        super.init(http);
        initDefaultLoginFilter(http);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return PathPatternRequestMatcher.withDefaults().matcher(HttpMethod.POST, loginProcessingUrl);
    }

    private String getUsernameParameter() {
        return getAuthenticationFilter().getUsernameParameter();
    }

    private String getPasswordParameter() {
        return getAuthenticationFilter().getPasswordParameter();
    }

    /**
     * If available, initializes the {@link DefaultLoginPageGeneratingFilter} shared
     * object.
     * @param http the {@link HttpSecurityBuilder} to use
     */
    private void initDefaultLoginFilter(H http) {
        DefaultLoginPageGeneratingFilter loginPageGeneratingFilter = http
                .getSharedObject(DefaultLoginPageGeneratingFilter.class);
        if (loginPageGeneratingFilter != null && !isCustomLoginPage()) {
            loginPageGeneratingFilter.setFormLoginEnabled(true);
            loginPageGeneratingFilter.setUsernameParameter(getUsernameParameter());
            loginPageGeneratingFilter.setPasswordParameter(getPasswordParameter());
            loginPageGeneratingFilter.setLoginPageUrl(getLoginPage());
            loginPageGeneratingFilter.setFailureUrl(getFailureUrl());
            loginPageGeneratingFilter.setAuthenticationUrl(getLoginProcessingUrl());
        }
    }

}
