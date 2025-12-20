package com.xtrarust.cloud.auth.security.authentication.handler;

import com.xtrarust.cloud.common.pojo.R;
import com.xtrarust.cloud.common.util.servlet.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * 自定义认证成功处理器
 *
 * @author gova
 */
@RequiredArgsConstructor
public class RestSimpleAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        ServletUtils.writeJSON(response, R.ok("authenticated", null));
    }
}
