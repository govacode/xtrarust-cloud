package com.xtrarust.cloud.auth.security.oauth2.server.resource;

import com.xtrarust.cloud.common.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义资源服务器{@link AccessDeniedHandler}实现
 *
 * @see BearerTokenAccessDeniedHandler
 */
@Slf4j
@Setter
public class CustomBearerTokenAccessDeniedHandler implements AccessDeniedHandler {

    private String realmName;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {
        log.info("Resource server handle {} using CustomBearerTokenAccessDeniedHandler", accessDeniedException.getClass().getSimpleName());
        Map<String, String> parameters = new LinkedHashMap<>();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        if (request.getUserPrincipal() instanceof AbstractOAuth2TokenAuthenticationToken) {
            parameters.put("error", BearerTokenErrorCodes.INSUFFICIENT_SCOPE);
            parameters.put("error_description",
                    "The request requires higher privileges than provided by the access token.");
            parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
        }
        String wwwAuthenticate = CustomBearerTokenAuthenticationEntryPoint.computeWWWAuthenticateHeaderValue(parameters);
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.setStatus(HttpStatus.FORBIDDEN.value());

        // 不同于框架默认实现BearerTokenAccessDeniedHandler仅在响应头输出错误信息 这里还将错误信息以JSON形式输出
        ServletUtils.writeJSON(response, parameters);
    }

}
