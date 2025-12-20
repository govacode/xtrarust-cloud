package com.xtrarust.cloud.auth.security.oauth2.client.endpoint;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationExchange;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * QQ授权码换access_token
 *
 * @author gova
 */
public class QQAuthorizationCodeTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private final RestOperations restOperations;

    public QQAuthorizationCodeTokenResponseClient() {
        RestTemplate restTemplate = new RestTemplate(List.of(new FormHttpMessageConverter(), new TextHtmlHttpMessageConverter()));
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());
        this.restOperations = restTemplate;
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        Assert.notNull(authorizationCodeGrantRequest, "authorizationCodeGrantRequest cannot be null");

        // https://wiki.open.qq.com/wiki/%E3%80%90QQ%E7%99%BB%E5%BD%95%E3%80%91%E4%BD%BF%E7%94%A8Authorization_Code%E8%8E%B7%E5%8F%96Access_Token
        String url = authorizationCodeGrantRequest.getClientRegistration().getProviderDetails().getTokenUri();
        MultiValueMap<String, String> formParameters = buildFormParameters(authorizationCodeGrantRequest);
        // eg: access_token=FE04************************CCE2&expires_in=7776000
        String tokenResponse = restOperations.postForObject(url, formParameters, String.class);
        String[] items = StringUtils.split(tokenResponse, "&");
        Assert.isTrue(items != null && items.length >= 2, "获取accessToken失败");
        String accessToken = StringUtils.substringAfter(items[0], "=");
        String expiresIn = StringUtils.substringAfter(items[1], "=");

        Set<String> scopes = authorizationCodeGrantRequest.getAuthorizationExchange().getAuthorizationRequest().getScopes();
        return OAuth2AccessTokenResponse.withToken(accessToken)
                .tokenType(OAuth2AccessToken.TokenType.BEARER)
                .expiresIn(Long.parseLong(expiresIn))
                .scopes(scopes)
                .additionalParameters(new LinkedHashMap<>())
                .build();
    }

    private MultiValueMap<String, String> buildFormParameters(OAuth2AuthorizationCodeGrantRequest authorizationCodeGrantRequest) {
        ClientRegistration clientRegistration = authorizationCodeGrantRequest.getClientRegistration();
        OAuth2AuthorizationExchange authorizationExchange = authorizationCodeGrantRequest.getAuthorizationExchange();

        MultiValueMap<String, String> formParameters = new LinkedMultiValueMap<>();
        formParameters.add(OAuth2ParameterNames.GRANT_TYPE, authorizationCodeGrantRequest.getGrantType().getValue());
        formParameters.add(OAuth2ParameterNames.CODE, authorizationExchange.getAuthorizationResponse().getCode());
        formParameters.add(OAuth2ParameterNames.CLIENT_ID, clientRegistration.getClientId());
        formParameters.add(OAuth2ParameterNames.CLIENT_SECRET, clientRegistration.getClientSecret());
        String redirectUri = authorizationExchange.getAuthorizationRequest().getRedirectUri();
        formParameters.add(OAuth2ParameterNames.REDIRECT_URI, redirectUri);
        return formParameters;
    }
}
