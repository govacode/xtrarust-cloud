package com.xtrarust.cloud.auth.security.oauth2.client.endpoint;

import com.xtrarust.cloud.auth.security.oauth2.client.Oauth2ClientConstants;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.endpoint.RestClientAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * {@link OAuth2AccessTokenResponseClient}是Spring Security Oauth2 Client对授权码换access_token的抽象
 * {@link RestClientAuthorizationCodeTokenResponseClient} 为框架默认实现
 *
 * @author gova
 * @see RestClientAuthorizationCodeTokenResponseClient#getTokenResponse(OAuth2AuthorizationCodeGrantRequest)
 */
public class DelegatingOAuth2AccessTokenResponseClient implements OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> {

    private static final List<MediaType> SUPPORTED_MEDIA_TYPES = List.of(
            MediaType.TEXT_PLAIN, // 兼容微信
            MediaType.APPLICATION_JSON,
            new MediaType("application", "*+json")
    );

    private final RestClientAuthorizationCodeTokenResponseClient defaultClient = new RestClientAuthorizationCodeTokenResponseClient();

    public DelegatingOAuth2AccessTokenResponseClient() {
        defaultClient.setParametersConverter(new EnhancedOAuth2TokenRequestParametersConverter());

        // 微信access_token接口返回数据虽然是json格式但Content-Type是text/plain
        OAuth2AccessTokenResponseHttpMessageConverter httpMessageConverter = new OAuth2AccessTokenResponseHttpMessageConverter();
        httpMessageConverter.setSupportedMediaTypes(SUPPORTED_MEDIA_TYPES);
        // 将返回的map转换为OAuth2AccessTokenResponse
        httpMessageConverter.setAccessTokenResponseConverter(new EnhancedMapOAuth2AccessTokenResponseConverter());
        RestClient restClient = RestClient.builder()
                .messageConverters((messageConverters) -> {
                    messageConverters.clear();
                    messageConverters.add(new FormHttpMessageConverter());
                    messageConverters.add(httpMessageConverter);
                })
                .defaultStatusHandler(new OAuth2ErrorResponseErrorHandler())
                .build();
        defaultClient.setRestClient(restClient);
    }

    @Override
    public OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizationCodeGrantRequest grantRequest) {
        String registrationId = grantRequest.getClientRegistration().getRegistrationId();
        if (Oauth2ClientConstants.REGISTRATION_ID_QQ.equals(registrationId)) {
            return new QQAuthorizationCodeTokenResponseClient().getTokenResponse(grantRequest);
        }
        return defaultClient.getTokenResponse(grantRequest);
    }
}
