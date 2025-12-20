package com.xtrarust.cloud.auth.security.authentication.handler;

import com.xtrarust.cloud.common.domain.R;
import com.xtrarust.cloud.common.exception.errorcode.BaseErrorCode;
import com.xtrarust.cloud.common.util.servlet.ServletUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ServletUtils.writeJSON(response, R.failed(BaseErrorCode.FORBIDDEN));
    }
}
