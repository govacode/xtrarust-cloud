package com.xtrarust.cloud.auth.security.oauth2.client.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.DefaultMapOAuth2AccessTokenResponseConverter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

import java.util.Map;

/**
 * A Converter that converts the provided OAuth 2.0 Access Token Response parameters to an OAuth2AccessTokenResponse.
 * 兼容微信token端点响应报文
 *
 * @author gova
 */
@Slf4j
public class EnhancedMapOAuth2AccessTokenResponseConverter implements Converter<Map<String, Object>, OAuth2AccessTokenResponse> {

    private final Converter<Map<String, Object>, OAuth2AccessTokenResponse> defaultConverter = new DefaultMapOAuth2AccessTokenResponseConverter();

    /**
     * 微信通过code获取access_token响应示例：
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    @Override
    public OAuth2AccessTokenResponse convert(Map<String, Object> tokenResponseParameters) {
        // 若响应不包含 token_type 则默认添加
        if (!tokenResponseParameters.containsKey(OAuth2ParameterNames.TOKEN_TYPE)) {
            log.info("access token response does not contain token_type parameters");
            tokenResponseParameters.put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2AccessToken.TokenType.BEARER.getValue());
        }
        return this.defaultConverter.convert(tokenResponseParameters);
    }
}
