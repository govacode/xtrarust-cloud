package com.xtrarust.cloud.auth.security.oauth2.server.authentication.handler;

import com.xtrarust.cloud.common.domain.R;
import com.xtrarust.cloud.common.util.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * 授权请求及授权确认请求失败处理（框架默认实现见OAuth2AuthorizationEndpointFilter#sendAuthorizationResponse）
 *
 * @author gova
 */
public final class AuthorizationAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthorizationCodeRequestAuthenticationToken authorizationCodeRequestAuthentication = (OAuth2AuthorizationCodeRequestAuthenticationToken) authentication;
        assert authorizationCodeRequestAuthentication.getRedirectUri() != null;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromUriString(authorizationCodeRequestAuthentication.getRedirectUri())
                .queryParam(OAuth2ParameterNames.CODE,
                        authorizationCodeRequestAuthentication.getAuthorizationCode().getTokenValue());
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.getState())) {
            uriBuilder.queryParam(OAuth2ParameterNames.STATE,
                    UriUtils.encode(authorizationCodeRequestAuthentication.getState(), StandardCharsets.UTF_8));
        }
        // build(true) -> Components are explicitly encoded
        String redirectUri = uriBuilder.build(true).toUriString();
        ServletUtils.writeJSON(response, R.ok(Collections.singletonMap("targetUrl", redirectUri)));
    }
}
