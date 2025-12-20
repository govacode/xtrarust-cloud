package com.xtrarust.cloud.auth.security.oauth2.server.authentication.handler;

import com.xtrarust.cloud.common.pojo.R;
import com.xtrarust.cloud.common.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * 授权请求及授权确认请求失败处理（框架默认实现见OAuth2AuthorizationEndpointFilter#sendErrorResponse）
 *
 * @author gova
 * @see org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationProvider
 * @see org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationConsentAuthenticationProvider
 * @see org.springframework.security.oauth2.core.OAuth2ErrorCodes
 */
@Slf4j
public final class AuthorizationAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        OAuth2AuthorizationCodeRequestAuthenticationException authenticationException = (OAuth2AuthorizationCodeRequestAuthenticationException) exception;
        OAuth2Error error = authenticationException.getError();
        ServletUtils.writeJSON(response, R.failed(error.getErrorCode(), error.getDescription()));
    }

}
