package com.xtrarust.cloud.sms.handler;

import com.xtrarust.cloud.common.exception.errorcode.BaseErrorCode;
import com.xtrarust.cloud.common.domain.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.comm.exception.SmsBlendException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * SMS异常处理器
 *
 * @author gova
 */
@Slf4j
@RestControllerAdvice
public class SmsExceptionHandler {

    @ExceptionHandler(SmsBlendException.class)
    public R<Void> handleSmsBlendException(SmsBlendException e, HttpServletRequest request) {
        log.error("SMS短信发送异常, 请求地址: {}", request.getRequestURI(), e);
        return R.failed(BaseErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }

}
