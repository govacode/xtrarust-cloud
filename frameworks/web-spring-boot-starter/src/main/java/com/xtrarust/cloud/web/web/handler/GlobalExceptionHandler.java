package com.xtrarust.cloud.web.web.handler;

import com.xtrarust.cloud.common.exception.AbstractException;
import com.xtrarust.cloud.common.exception.ClientException;
import com.xtrarust.cloud.common.exception.ServiceException;
import com.xtrarust.cloud.common.exception.errorcode.BaseErrorCode;
import com.xtrarust.cloud.common.pojo.R;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 SpringMVC 请求参数缺失
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.warn("missing request parameter, ", e);
        return abstractExceptionHandler(new ClientException(BaseErrorCode.MISSING_REQUEST_PARAMETER, e, new String[]{e.getParameterName()}));
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        log.warn("method argument type mismatch, ", e);
        return abstractExceptionHandler(new ClientException(BaseErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH, e, new String[]{e.getParameter().getParameterName()}));
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> methodArgumentNotValidExceptionExceptionHandler(MethodArgumentNotValidException e) {
        log.warn("request param is invalid, ", e);
        String errMsg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return abstractExceptionHandler(new ClientException(BaseErrorCode.REQUEST_PARAM_NOT_VALID, e, new String[]{errMsg}));
    }

    /**
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> bindExceptionHandler(BindException e) {
        log.warn("request param is invalid, ", e);
        String errMsg = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return abstractExceptionHandler(new ClientException(BaseErrorCode.REQUEST_PARAM_NOT_VALID, e, new String[]{errMsg}));
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
        log.warn("request param is invalid, ", e);
        ConstraintViolation<?> constraintViolation = e.getConstraintViolations().iterator().next();
        return abstractExceptionHandler(new ClientException(BaseErrorCode.REQUEST_PARAM_NOT_VALID, e, new String[]{constraintViolation.getMessage()}));
    }

    /**
     * 处理 SpringMVC 请求地址不存在
     *
     * 注意，它需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
     * 2. spring.mvc.static-path-pattern 为 /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> noHandlerFoundExceptionHandler(NoHandlerFoundException e) {
        log.warn("no handler found, ", e);
        return abstractExceptionHandler(new ClientException(BaseErrorCode.NO_HANDLER_FOUND, e, new String[]{e.getRequestURL()}));
    }

    /**
     * 处理 SpringMVC 请求方法不正确
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<?> httpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        log.warn("request method not supported, ", e);
        return abstractExceptionHandler(new ClientException(BaseErrorCode.REQUEST_METHOD_NOT_SUPPORTED, e));
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = AbstractException.class)
    public R<?> abstractExceptionHandler(AbstractException e) {
        log.error("[abstractExceptionHandler], cause: {}", e.getCause().getClass().getSimpleName(), e);
        return R.failed(e);
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> defaultExceptionHandler(Throwable e) throws Throwable {
        if (e instanceof AccessDeniedException) {
            throw e; // 不处理 Spring Security 权限不足的异常
        }
        log.error("[defaultExceptionHandler]", e);
        return abstractExceptionHandler(new ServiceException(BaseErrorCode.SERVICE_ERROR, e));
    }

}
