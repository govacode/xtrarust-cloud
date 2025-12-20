package com.xtrarust.cloud.auth.security.authentication.sms;

import com.xtrarust.cloud.auth.security.userdetails.MobileNotFoundException;
import com.xtrarust.cloud.auth.security.userdetails.MobileUserDetailsService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

@Setter
@Getter
public class MobileSmsCodeAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    private SmsCodeService smsCodeService;

    private MobileUserDetailsService mobileUserDetailsService;

    public MobileSmsCodeAuthenticationProvider() {
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobileSmsCodeAuthenticationToken token = (MobileSmsCodeAuthenticationToken) authentication;
        String mobile = (String) token.getPrincipal();
        String smsCode = (String) token.getCredentials();

        if (!this.getSmsCodeService().verifySmsCode(mobile, smsCode)) {
            throw new BadCredentialsException(messages.getMessage("MobileSmsCodeAuthenticationProvider.InvalidSmsCode", "Invalid sms code"));
        }

        UserDetails loadedUser;
        try {
            loadedUser = this.getMobileUserDetailsService().loadUserByMobile(mobile);
            if (loadedUser == null) {
                // MobileUserDetailsService实现类不应返回空 当用户不存在时应抛出MobileNotFoundException
                throw new InternalAuthenticationServiceException("MobileUserDetailsService returned null, which is an interface contract violation");
            }
        } catch (MobileNotFoundException | InternalAuthenticationServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex);
        }

        MobileSmsCodeAuthenticationToken result = new MobileSmsCodeAuthenticationToken(
                loadedUser, smsCode, loadedUser.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MobileSmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.smsCodeService, "A SmsCodeService must be set");
    }
}
