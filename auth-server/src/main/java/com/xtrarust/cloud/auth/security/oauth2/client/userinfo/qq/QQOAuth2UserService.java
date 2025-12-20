package com.xtrarust.cloud.auth.security.oauth2.client.userinfo.qq;

import com.xtrarust.cloud.auth.security.oauth2.client.userinfo.TextHtmlMappingJackson2HttpMessageConverter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class QQOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String GET_USERINFO_URL =
            "https://graph.qq.com/user/get_user_info?access_token={accessToken}&oauth_consumer_key={appId}&openid={openId}";

    private final RestOperations restOperations;

    public QQOAuth2UserService() {
        RestTemplate restTemplate = new RestTemplate(Collections.singletonList(new TextHtmlMappingJackson2HttpMessageConverter()));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String url = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri() + "?access_token={accessToken}";
        // eg: callback( {"client_id":"YOUR_APPID","openid":"YOUR_OPENID"} );
        String response = restOperations.getForObject(url, String.class, accessToken);
        String openid = StringUtils.substringBetween(response, "\"openid\":\"", "\"}");

        String appId = userRequest.getClientRegistration().getClientId();
        QQUserInfo userInfo = restOperations.getForObject(GET_USERINFO_URL, QQUserInfo.class, accessToken, appId, openid);
        if (userInfo != null) {
            userInfo.setOpenid(openid);
        }
        return userInfo;
    }
}
