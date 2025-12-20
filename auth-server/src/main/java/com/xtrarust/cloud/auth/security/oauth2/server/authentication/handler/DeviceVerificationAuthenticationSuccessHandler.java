package com.xtrarust.cloud.auth.security.oauth2.server.authentication.handler;

import com.xtrarust.cloud.common.domain.R;
import com.xtrarust.cloud.common.util.servlet.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 设备验证成功处理
 *
 * @author gova
 */
@RequiredArgsConstructor
public class DeviceVerificationAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final String deviceActivatedUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        ServletUtils.writeJSON(response, R.ok(deviceActivatedUri));
    }
}
