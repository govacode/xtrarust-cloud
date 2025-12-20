package com.xtrarust.cloud.auth.security.authentication.handler;

import com.xtrarust.cloud.auth.common.enums.SecurityErrorCode;
import com.xtrarust.cloud.common.pojo.R;
import com.xtrarust.cloud.common.util.servlet.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * 自定义认证失败处理器
 *
 * @author gova
 */
@Slf4j
public class RestSimpleAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        if (log.isDebugEnabled()) {
            log.debug("认证失败，", exception);
        }

        // response.setStatus(HttpStatus.UNAUTHORIZED.value());
        ServletUtils.writeJSON(response, R.failed(SecurityErrorCode.UNAUTHORIZED.getCode(), exception.getMessage()));
    }

}
