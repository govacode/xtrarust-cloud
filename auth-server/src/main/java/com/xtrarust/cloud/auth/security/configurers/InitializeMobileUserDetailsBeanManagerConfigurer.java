package com.xtrarust.cloud.auth.security.configurers;

import com.xtrarust.cloud.auth.security.authentication.sms.MobileSmsCodeAuthenticationProvider;
import com.xtrarust.cloud.auth.security.authentication.sms.SmsCodeService;
import com.xtrarust.cloud.auth.security.userdetails.MobileUserDetailsService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;

/**
 * 用于初始化{@link MobileSmsCodeAuthenticationProvider}并添加到{@link AuthenticationManager}
 * 重要：优先级要低于InitializeUserDetailsBeanManagerConfigurer 必须先于其执行
 *
 * @author gova
 * @see org.springframework.security.config.annotation.authentication.configuration.InitializeUserDetailsBeanManagerConfigurer
 */
@Order(InitializeMobileUserDetailsBeanManagerConfigurer.DEFAULT_ORDER)
public class InitializeMobileUserDetailsBeanManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

    static final int DEFAULT_ORDER = Ordered.LOWEST_PRECEDENCE - 4000;

    private final ApplicationContext context;

    public InitializeMobileUserDetailsBeanManagerConfigurer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.apply(new InitializeMobileUserDetailsManagerConfigurer());
    }

    class InitializeMobileUserDetailsManagerConfigurer extends GlobalAuthenticationConfigurerAdapter {

        @Override
        public void configure(AuthenticationManagerBuilder auth) throws Exception {
            MobileUserDetailsService mobileUserDetailsService = getBeanOrNull(MobileUserDetailsService.class);
            if (mobileUserDetailsService == null) {
                return;
            }
            SmsCodeService smsCodeService = getBeanOrNull(SmsCodeService.class);
            MessageSource messageSource = getBeanOrNull(MessageSource.class);
            MobileSmsCodeAuthenticationProvider provider = new MobileSmsCodeAuthenticationProvider();
            provider.setMobileUserDetailsService(mobileUserDetailsService);
            if (smsCodeService != null) {
                provider.setSmsCodeService(smsCodeService);
            }
            if (messageSource != null) {
                provider.setMessages(new MessageSourceAccessor(messageSource));
            }
            provider.afterPropertiesSet();
            auth.authenticationProvider(provider);
        }

        /**
         * @return a bean of the requested class if there's just a single registered
         * component, null otherwise.
         */
        private <T> T getBeanOrNull(Class<T> type) {
            return InitializeMobileUserDetailsBeanManagerConfigurer.this.context.getBeanProvider(type).getIfUnique();
        }

    }
}
