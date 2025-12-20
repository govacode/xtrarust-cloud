package com.xtrarust.cloud.auth.security.configurers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

/**
 * copy from {@link org.springframework.security.config.annotation.web.configurers.PermitAllSupport}
 *
 * @author wdxu
 */
public final class PermitAllSupports {

    private PermitAllSupports() {
    }

    public static void permitAll(HttpSecurityBuilder<? extends HttpSecurityBuilder<?>> http, String... urls) {
        for (String url : urls) {
            if (url != null) {
                permitAll(http, new ExactUrlRequestMatcher(url));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static void permitAll(HttpSecurityBuilder<? extends HttpSecurityBuilder<?>> http,
                          RequestMatcher... requestMatchers) {
        ExpressionUrlAuthorizationConfigurer<?> configurer = http
                .getConfigurer(ExpressionUrlAuthorizationConfigurer.class);
        Assert.state(configurer != null, "permitAll only works with HttpSecurity.authorizeRequests()");
        configurer.getRegistry().requestMatchers(requestMatchers).permitAll();
    }

    private static final class ExactUrlRequestMatcher implements RequestMatcher {

        private final String processUrl;

        private ExactUrlRequestMatcher(String processUrl) {
            this.processUrl = processUrl;
        }

        @Override
        public boolean matches(HttpServletRequest request) {
            String uri = request.getRequestURI();
            String query = request.getQueryString();
            if (query != null) {
                uri += "?" + query;
            }
            if ("".equals(request.getContextPath())) {
                return uri.equals(this.processUrl);
            }
            return uri.equals(request.getContextPath() + this.processUrl);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ExactUrl [processUrl='").append(this.processUrl).append("']");
            return sb.toString();
        }

    }
}
