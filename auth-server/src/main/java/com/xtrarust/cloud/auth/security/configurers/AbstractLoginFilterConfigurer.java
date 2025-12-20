package com.xtrarust.cloud.auth.security.configurers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.PortMapper;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.DelegatingSecurityContextRepository;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.*;
import org.springframework.web.accept.ContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import java.util.Arrays;
import java.util.Collections;

/**
 * copy from {@link AbstractAuthenticationFilterConfigurer} 去除其仅支持内部过滤器限制
 *
 * @param <B> the HttpSecurityBuilder
 * @param <T> the AbstractAuthenticationProcessingFilterConfigurer
 * @param <F> the AbstractAuthenticationProcessingFilter
 * @author gova
 */
public abstract class AbstractLoginFilterConfigurer<
        B extends HttpSecurityBuilder<B>,
        T extends AbstractLoginFilterConfigurer<B, T, F>,
        F extends AbstractAuthenticationProcessingFilter
        > extends AbstractHttpConfigurer<T, B> {

    private F authFilter;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private SavedRequestAwareAuthenticationSuccessHandler defaultSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    private AuthenticationSuccessHandler successHandler = this.defaultSuccessHandler;

    private LoginUrlAuthenticationEntryPoint authenticationEntryPoint;

    private boolean customLoginPage;

    private String loginPage;

    private String loginProcessingUrl;

    private AuthenticationFailureHandler failureHandler;

    private boolean permitAll;

    private String failureUrl;

    /**
     * 创建一个配置器实例
     *
     * @param authFilter the {@link AbstractAuthenticationProcessingFilter} to use
     * @param defaultLoginProcessingUrl the default URL to use for
     */
    protected AbstractLoginFilterConfigurer(F authFilter, String defaultLoginProcessingUrl) {
        setLoginPage("/login");
        this.authFilter = authFilter;
        if (defaultLoginProcessingUrl != null) {
            loginProcessingUrl(defaultLoginProcessingUrl);
        }
    }

    /**
     * 指定认证成功后重定向url如果用户在认证前没有访问一个受保护页面的话
     *
     * @param defaultSuccessUrl 默认认证成功重定向url
     * @return 配置器对象用以进行额外配置（即支持链式调用）
     */
    public final T defaultSuccessUrl(String defaultSuccessUrl) {
        return defaultSuccessUrl(defaultSuccessUrl, false);
    }

    /**
     * 指定认证成功后重定向url如果用户在认证前没有访问一个受保护页面或者{@code alwaysUse}为true的话
     *
     * {@link #successHandler(AuthenticationSuccessHandler)}.
     * @param defaultSuccessUrl 默认认证成功重定向url
     * @param alwaysUse 是否认证成功始终重定向至指定url
     * @return 配置器对象用以进行额外配置
     */
    public final T defaultSuccessUrl(String defaultSuccessUrl, boolean alwaysUse) {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(defaultSuccessUrl);
        handler.setAlwaysUseDefaultTargetUrl(alwaysUse);
        this.defaultSuccessHandler = handler;
        return successHandler(handler);
    }

    /**
     * 指定登录认证处理url
     *
     * @param loginProcessingUrl 登录认证处理url
     * @return 配置器对象用以进行额外配置
     */
    public T loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.authFilter.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return getSelf();
    }

    /**
     * 用给定的登录认证处理url创建{@link RequestMatcher} （抽象方法子类实现）
     *
     * @param loginProcessingUrl 用以创建 {@link RequestMatcher} 的认证处理url
     * @return 创建的{@link RequestMatcher}
     */
    protected abstract RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl);

    /**
     * 指定自定义 {@link AuthenticationDetailsSource} 默认为 {@link WebAuthenticationDetailsSource}
     *
     * @param authenticationDetailsSource 自定义 {@link AuthenticationDetailsSource}
     * @return 配置器对象用以进行额外配置
     */
    public final T authenticationDetailsSource(
            AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource) {
        this.authenticationDetailsSource = authenticationDetailsSource;
        return getSelf();
    }

    /**
     * 指定认证成功处理器 {@link AuthenticationSuccessHandler} 默认为 {@link SavedRequestAwareAuthenticationSuccessHandler}
     *
     * @param successHandler 认证成功处理器
     * @return 配置器对象用以进行额外配置
     */
    public final T successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return getSelf();
    }

    /**
     * 等同于调用 permitAll(true)
     *
     * @return 配置器对象用以进行额外配置
     */
    public final T permitAll() {
        return permitAll(true);
    }

    /**
     * 确保 {@link #failureUrl(String)} 和 {@link #getLoginProcessingUrl} 可以被任何用户访问
     *
     * @param permitAll true 以授予对 URL 的访问权限 false 以跳过此步骤
     * @return 配置器对象用以进行额外配置
     */
    public final T permitAll(boolean permitAll) {
        this.permitAll = permitAll;
        return getSelf();
    }

    /**
     * 指定认证失败重定向url 默认为"/login?error"
     *
     * @param authenticationFailureUrl 认证失败重定向url
     * @return 配置器对象用以进行额外配置
     */
    public final T failureUrl(String authenticationFailureUrl) {
        T result = failureHandler(new SimpleUrlAuthenticationFailureHandler(authenticationFailureUrl));
        this.failureUrl = authenticationFailureUrl;
        return result;
    }

    /**
     * 指定认证失败处理器 {@link AuthenticationFailureHandler}
     *
     * @param authenticationFailureHandler 认证失败处理器
     * @return 配置器对象用以进行额外配置
     */
    public final T failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureUrl = null;
        this.failureHandler = authenticationFailureHandler;
        return getSelf();
    }

    @Override
    public void init(B http) throws Exception {
        updateAuthenticationDefaults();
        updateAccessDefaults(http);
        registerDefaultAuthenticationEntryPoint(http);
    }

    @SuppressWarnings("unchecked")
    protected final void registerDefaultAuthenticationEntryPoint(B http) {
        registerAuthenticationEntryPoint(http, this.authenticationEntryPoint);
    }

    @SuppressWarnings("unchecked")
    protected final void registerAuthenticationEntryPoint(B http, AuthenticationEntryPoint authenticationEntryPoint) {
        ExceptionHandlingConfigurer<B> exceptionHandling = http.getConfigurer(ExceptionHandlingConfigurer.class);
        if (exceptionHandling == null) {
            return;
        }
        exceptionHandling.defaultAuthenticationEntryPointFor(postProcess(authenticationEntryPoint),
                getAuthenticationEntryPointMatcher(http));
    }

    protected final RequestMatcher getAuthenticationEntryPointMatcher(B http) {
        ContentNegotiationStrategy contentNegotiationStrategy = http.getSharedObject(ContentNegotiationStrategy.class);
        if (contentNegotiationStrategy == null) {
            contentNegotiationStrategy = new HeaderContentNegotiationStrategy();
        }
        MediaTypeRequestMatcher mediaMatcher = new MediaTypeRequestMatcher(contentNegotiationStrategy,
                MediaType.APPLICATION_XHTML_XML, new MediaType("image", "*"), MediaType.TEXT_HTML,
                MediaType.TEXT_PLAIN);
        mediaMatcher.setIgnoredMediaTypes(Collections.singleton(MediaType.ALL));
        RequestMatcher notXRequestedWith = new NegatedRequestMatcher(
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));
        return new AndRequestMatcher(Arrays.asList(notXRequestedWith, mediaMatcher));
    }

    @Override
    public void configure(B http) throws Exception {
        PortMapper portMapper = http.getSharedObject(PortMapper.class);
        if (portMapper != null) {
            this.authenticationEntryPoint.setPortMapper(portMapper);
        }
        RequestCache requestCache = http.getSharedObject(RequestCache.class);
        if (requestCache != null) {
            this.defaultSuccessHandler.setRequestCache(requestCache);
        }
        this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
        this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
        if (this.authenticationDetailsSource != null) {
            this.authFilter.setAuthenticationDetailsSource(this.authenticationDetailsSource);
        }
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            this.authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            this.authFilter.setRememberMeServices(rememberMeServices);
        }
        this.authFilter.setSecurityContextRepository(new DelegatingSecurityContextRepository(
                new RequestAttributeSecurityContextRepository(), new HttpSessionSecurityContextRepository()));
        this.authFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        F filter = postProcess(this.authFilter);
        http.addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);
    }

    protected T loginPage(String loginPage) {
        setLoginPage(loginPage);
        updateAuthenticationDefaults();
        this.customLoginPage = true;
        return getSelf();
    }

    /**
     * @return true if a custom login page has been specified, else false
     */
    public final boolean isCustomLoginPage() {
        return this.customLoginPage;
    }

    /**
     * Gets the Authentication Filter
     * @return the Authentication Filter
     */
    protected final F getAuthenticationFilter() {
        return this.authFilter;
    }

    /**
     * Sets the Authentication Filter
     * @param authFilter the Authentication Filter
     */
    protected final void setAuthenticationFilter(F authFilter) {
        this.authFilter = authFilter;
    }

    /**
     * Gets the login page
     * @return the login page
     */
    protected final String getLoginPage() {
        return this.loginPage;
    }

    /**
     * Gets the Authentication Entry Point
     * @return the Authentication Entry Point
     */
    protected final AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return this.authenticationEntryPoint;
    }

    /**
     * Gets the URL to submit an authentication request to (i.e. where username/password
     * must be submitted)
     * @return the URL to submit an authentication request to
     */
    protected final String getLoginProcessingUrl() {
        return this.loginProcessingUrl;
    }

    /**
     * Gets the URL to send users to if authentication fails
     * @return the URL to send users if authentication fails (e.g. "/login?error").
     */
    protected final String getFailureUrl() {
        return this.failureUrl;
    }

    /**
     * Updates the default values for authentication.
     */
    protected final void updateAuthenticationDefaults() {
        if (this.loginProcessingUrl == null) {
            loginProcessingUrl(this.loginPage);
        }
        if (this.failureHandler == null) {
            failureUrl(this.loginPage + "?error");
        }
    }

    /**
     * Updates the default values for access.
     */
    protected final void updateAccessDefaults(B http) {
        if (this.permitAll) {
            PermitAllSupports.permitAll(http, this.loginPage, this.loginProcessingUrl, this.failureUrl);
        }
    }

    /**
     * Sets the loginPage and updates the {@link AuthenticationEntryPoint}.
     * @param loginPage
     */
    private void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
        this.authenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(loginPage);
    }

    @SuppressWarnings("unchecked")
    private T getSelf() {
        return (T) this;
    }

}
