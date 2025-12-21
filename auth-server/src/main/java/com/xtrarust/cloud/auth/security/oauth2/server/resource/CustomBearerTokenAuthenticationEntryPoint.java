package com.xtrarust.cloud.auth.security.oauth2.server.resource;

import com.xtrarust.cloud.common.util.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrorCodes;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义资源服务器{@link AuthenticationEntryPoint}实现
 *
 * @see BearerTokenAuthenticationEntryPoint
 */
@Slf4j
@Setter
public class CustomBearerTokenAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private String realmName;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {
        log.info("Resource server handle {} using CustomBearerTokenAuthenticationEntryPoint", authException.getClass().getSimpleName());
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        Map<String, String> parameters = new LinkedHashMap<>();
        if (this.realmName != null) {
            parameters.put("realm", this.realmName);
        }
        // BearerTokenAuthenticationFilter解析bearer token失败及token认证失败时均会抛出OAuth2AuthenticationException
        // 如携带非法token，JwtAuthenticationProvider解码jwt时捕获到BadJwtException会抛出InvalidBearerTokenException
        if (authException instanceof OAuth2AuthenticationException e) {
            OAuth2Error error = e.getError();
            parameters.put("error", error.getErrorCode());
            if (StringUtils.hasText(error.getDescription())) {
                parameters.put("error_description", error.getDescription());
            }
            if (StringUtils.hasText(error.getUri())) {
                parameters.put("error_uri", error.getUri());
            }
            if (error instanceof BearerTokenError bearerTokenError) {
                if (StringUtils.hasText(bearerTokenError.getScope())) {
                    parameters.put("scope", bearerTokenError.getScope());
                }
                status = bearerTokenError.getHttpStatus();
            }
        }
        // ExceptionTranslationFilter#handleAccessDeniedException时匿名用户也会进入AuthenticationEntryPoint#commence处理逻辑
        // 如不携带token访问同时Accept=application/json会进入下面的处理逻辑
        // 但浏览器直接访问会返回登录页（框架默认为DelegatingAuthenticationEntryPoint）
        if (authException instanceof InsufficientAuthenticationException) {
            parameters.put("error", BearerTokenErrorCodes.INVALID_REQUEST);
            parameters.put("error_description", "Not authorized.");
            parameters.put("error_uri", "https://tools.ietf.org/html/rfc6750#section-3.1");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
        String wwwAuthenticate = computeWWWAuthenticateHeaderValue(parameters);
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, wwwAuthenticate);
        response.setStatus(status.value());

        // 不同于框架默认实现BearerTokenAuthenticationEntryPoint仅在响应头输出错误信息 这里还将错误信息以JSON形式输出
        ServletUtils.writeJSON(response, parameters);
    }

    public static String computeWWWAuthenticateHeaderValue(Map<String, String> parameters) {
        StringBuilder wwwAuthenticate = new StringBuilder();
        wwwAuthenticate.append("Bearer");
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ");
            int i = 0;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                wwwAuthenticate.append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
                if (i != parameters.size() - 1) {
                    wwwAuthenticate.append(", ");
                }
                i++;
            }
        }
        return wwwAuthenticate.toString();
    }
}
