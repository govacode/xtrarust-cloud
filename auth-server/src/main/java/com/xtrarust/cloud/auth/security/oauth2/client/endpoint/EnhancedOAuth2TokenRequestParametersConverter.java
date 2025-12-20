package com.xtrarust.cloud.auth.security.oauth2.client.endpoint;

import com.xtrarust.cloud.auth.security.oauth2.client.Oauth2ClientConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.endpoint.DefaultOAuth2TokenRequestParametersConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class EnhancedOAuth2TokenRequestParametersConverter implements Converter<OAuth2AuthorizationCodeGrantRequest, MultiValueMap<String, String>> {

    private final Converter<OAuth2AuthorizationCodeGrantRequest, MultiValueMap<String, String>> defaultConverter = new DefaultOAuth2TokenRequestParametersConverter<>();

    // 授权码换access_token 构建请求参数
    @Override
    public MultiValueMap<String, String> convert(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        ClientRegistration clientRegistration = grantRequest.getClientRegistration();
        OAuth2AuthorizationExchange authorizationExchange = grantRequest.getAuthorizationExchange();
        String registrationId = clientRegistration.getRegistrationId();
        // 支持微信通过授权码code获取access_token请求参数appid、secret、code、grant_type
        if (Oauth2ClientConstants.REGISTRATION_ID_WECHAT.equals(registrationId)) {
            return createWechatParameters(clientRegistration, authorizationExchange, grantRequest.getGrantType().getValue());
        }
        return defaultConverter.convert(grantRequest);
    }

    private static MultiValueMap<String, String> createWechatParameters(ClientRegistration clientRegistration,
                                                                        OAuth2AuthorizationExchange authorizationExchange,
                                                                        String grantType) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add(Oauth2ClientConstants.PARAMETER_NAME_APPID, clientRegistration.getClientId());
        parameters.add(Oauth2ClientConstants.PARAMETER_NAME_SECRET, clientRegistration.getClientSecret());
        parameters.add(OAuth2ParameterNames.CODE, authorizationExchange.getAuthorizationResponse().getCode());
        parameters.add(OAuth2ParameterNames.GRANT_TYPE, grantType);
        String redirectUri = authorizationExchange.getAuthorizationRequest().getRedirectUri();
        if (redirectUri != null) {
            parameters.add(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
        }
        return parameters;
    }

}
