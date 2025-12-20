package com.xtrarust.cloud.auth.security.oauth2.client.userinfo;

import com.xtrarust.cloud.auth.security.oauth2.client.Oauth2ClientConstants;
import com.xtrarust.cloud.auth.security.oauth2.client.userinfo.qq.QQOAuth2UserService;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class DelegatingOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Map<String, OAuth2UserService<OAuth2UserRequest, OAuth2User>> serviceMap = new HashMap<>();

    static {
        serviceMap.put(Oauth2ClientConstants.REGISTRATION_ID_QQ, new QQOAuth2UserService());
    }

    private final DefaultOAuth2UserService defaultService = new DefaultOAuth2UserService();

    public DelegatingOAuth2UserService() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(new TextPlainMappingJackson2HttpMessageConverter()); // 兼容微信
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        defaultService.setRestOperations(restTemplate);

        defaultService.setRequestEntityConverter(new EnhancedOAuth2UserRequestEntityConverter());
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        return serviceMap.getOrDefault(registrationId, defaultService).loadUser(userRequest);
    }
}
