package com.xtrarust.cloud.auth.security.oauth2.client.userinfo;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.xtrarust.cloud.auth.security.oauth2.client.Oauth2ClientConstants.*;

/**
 * Oauth2三方登录用户信息请求转换器 兼容微信用户信息
 *
 * @author gova
 */
public class EnhancedOAuth2UserRequestEntityConverter extends OAuth2UserRequestEntityConverter {

    @Override
    public RequestEntity<?> convert(OAuth2UserRequest userRequest) {
        if (REGISTRATION_ID_WECHAT.equals(userRequest.getClientRegistration().getRegistrationId())) {
            // https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
            String userInfoUri = userRequest.getClientRegistration()
                    .getProviderDetails()
                    .getUserInfoEndpoint()
                    .getUri();

            MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
            queryParams.add(OAuth2ParameterNames.ACCESS_TOKEN, userRequest.getAccessToken().getTokenValue());
            String openid = (String) userRequest.getAdditionalParameters().get(PARAMETER_NAME_OPENID);
            queryParams.add(PARAMETER_NAME_OPENID, openid);
            queryParams.add(PARAMETER_NAME_LANG, PARAMETER_NAME_DEFAULT_LANG);
            URI uri = UriComponentsBuilder
                    .fromUriString(userInfoUri)
                    .queryParams(queryParams)
                    .build()
                    .toUri();
            return new RequestEntity<>(HttpMethod.GET, uri);
        }
        return super.convert(userRequest);
    }
}
