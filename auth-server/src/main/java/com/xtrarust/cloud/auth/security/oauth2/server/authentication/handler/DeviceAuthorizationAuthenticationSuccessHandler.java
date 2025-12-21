package com.xtrarust.cloud.auth.security.oauth2.server.authentication.handler;

import com.xtrarust.cloud.common.util.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2DeviceCode;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2DeviceAuthorizationResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2DeviceAuthorizationRequestAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备授权成功处理（框架默认实现见OAuth2DeviceAuthorizationEndpointFilter#sendDeviceAuthorizationResponse）
 *
 * @author gova
 */
@RequiredArgsConstructor
public class DeviceAuthorizationAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String verificationUri;

    private Converter<OAuth2DeviceAuthorizationResponse, Map<String, Object>> deviceAuthorizationResponseParametersConverter = new DefaultOAuth2DeviceAuthorizationResponseMapConverter();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2DeviceAuthorizationRequestAuthenticationToken deviceAuthorizationRequestAuthentication =
                (OAuth2DeviceAuthorizationRequestAuthenticationToken) authentication;

        OAuth2DeviceCode deviceCode = deviceAuthorizationRequestAuthentication.getDeviceCode();
        OAuth2UserCode userCode = deviceAuthorizationRequestAuthentication.getUserCode();

        // Generate the fully-qualified verification URI
        UriComponentsBuilder uriComponentsBuilder;
        if (UrlUtils.isAbsoluteUrl(verificationUri)) {
            // 设备验证页面前后端分离
            uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(verificationUri);
        } else {
            // 非前后端分离框架原有逻辑 拼接issuer和verificationUri
            String issuerUri = AuthorizationServerContextHolder.getContext().getIssuer();
            uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(issuerUri).path(this.verificationUri);
        }
        String verificationUri = uriComponentsBuilder.build().toUriString();
        // @formatter:off
        String verificationUriComplete = uriComponentsBuilder
                .queryParam(OAuth2ParameterNames.USER_CODE, userCode.getTokenValue())
                .build().toUriString();
        // @formatter:on

        // @formatter:off
        OAuth2DeviceAuthorizationResponse deviceAuthorizationResponse =
                OAuth2DeviceAuthorizationResponse.with(deviceCode, userCode)
                        .verificationUri(verificationUri)
                        .verificationUriComplete(verificationUriComplete)
                        .build();
        // @formatter:on

        ServletUtils.writeJSON(response, deviceAuthorizationResponseParametersConverter.convert(deviceAuthorizationResponse));
    }

    private static final class DefaultOAuth2DeviceAuthorizationResponseMapConverter
            implements Converter<OAuth2DeviceAuthorizationResponse, Map<String, Object>> {

        @Override
        public Map<String, Object> convert(OAuth2DeviceAuthorizationResponse deviceAuthorizationResponse) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put(OAuth2ParameterNames.DEVICE_CODE,
                    deviceAuthorizationResponse.getDeviceCode().getTokenValue());
            parameters.put(OAuth2ParameterNames.USER_CODE, deviceAuthorizationResponse.getUserCode().getTokenValue());
            parameters.put(OAuth2ParameterNames.VERIFICATION_URI, deviceAuthorizationResponse.getVerificationUri());
            if (StringUtils.hasText(deviceAuthorizationResponse.getVerificationUriComplete())) {
                parameters.put(OAuth2ParameterNames.VERIFICATION_URI_COMPLETE,
                        deviceAuthorizationResponse.getVerificationUriComplete());
            }
            parameters.put(OAuth2ParameterNames.EXPIRES_IN, getExpiresIn(deviceAuthorizationResponse));
            if (deviceAuthorizationResponse.getInterval() > 0) {
                parameters.put(OAuth2ParameterNames.INTERVAL, deviceAuthorizationResponse.getInterval());
            }
            if (!CollectionUtils.isEmpty(deviceAuthorizationResponse.getAdditionalParameters())) {
                parameters.putAll(deviceAuthorizationResponse.getAdditionalParameters());
            }
            return parameters;
        }

        private static long getExpiresIn(OAuth2DeviceAuthorizationResponse deviceAuthorizationResponse) {
            if (deviceAuthorizationResponse.getDeviceCode().getExpiresAt() != null) {
                Instant issuedAt = (deviceAuthorizationResponse.getDeviceCode().getIssuedAt() != null)
                        ? deviceAuthorizationResponse.getDeviceCode().getIssuedAt() : Instant.now();
                return ChronoUnit.SECONDS.between(issuedAt, deviceAuthorizationResponse.getDeviceCode().getExpiresAt());
            }
            return -1;
        }

    }
}
