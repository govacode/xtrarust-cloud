package com.xtrarust.cloud.web.web.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.xtrarust.cloud.common.exception.BaseException;
import com.xtrarust.cloud.common.exception.errorcode.BaseErrorCode;
import com.xtrarust.cloud.common.domain.R;
import com.xtrarust.cloud.common.util.StreamUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 SpringMVC 请求参数缺失
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("missing request parameter, ", e);
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "missing request parameter: " + e.getParameterName());
    }

    /**
     * 处理 SpringMVC 请求路径变量缺失
     */
    @ExceptionHandler(value = MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMissingPathVariableException(MissingPathVariableException e) {
        log.warn("missing path variable, ", e);
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "missing path variable: " + e.getVariableName());
    }

    /**
     * 处理 SpringMVC 请求参数类型错误
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("method argument type mismatch, ", e);
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "type mismatch: " + e.getName());
    }

    /**
     * 处理请求体读取异常
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("http message not readable, uri: {}", request.getRequestURI(), e);
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "http message not readable: " + e.getMostSpecificCause().getMessage());
    }

    /**
     * 处理 Jackson JSON解析异常
     */
    @ExceptionHandler(JsonParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleJsonParseException(JsonParseException e, HttpServletRequest request) {
        log.warn("json parse error, uri: {}", request.getRequestURI(), e);
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "json parse error: " + e.getMessage());
    }

    /**
     * 处理 SpringMVC 参数校验不正确
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("invalid request param, ", e);
        String message = StreamUtils.join(e.getBindingResult().getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "invalid argument: " + message);
    }

    /**
     * 处理 SpringMVC 参数绑定不正确，本质上也是通过 Validator 校验
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleBindException(BindException e) {
        log.warn("request param is invalid, ", e);
        String message = StreamUtils.join(e.getAllErrors(), DefaultMessageSourceResolvable::getDefaultMessage, ", ");
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "invalid argument: " + message);
    }

    /**
     * 处理 Validator 校验不通过产生的异常
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("request param is invalid, ", e);
        String message = StreamUtils.join(e.getConstraintViolations(), ConstraintViolation::getMessage, ", ");
        return R.failed(BaseErrorCode.BAD_REQUEST.getCode(), "invalid argument: " + message);
    }

    /**
     * 处理 SpringMVC 请求地址不存在<br>
     *
     * 注意，它需要设置如下两个配置项：
     * 1. spring.mvc.throw-exception-if-no-handler-found 为 true
     * 2. spring.mvc.static-path-pattern 为 /statics/**
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public R<?> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.warn("no handler found, ", e);
        return R.failed(BaseErrorCode.NOT_FOUND);
    }

    /**
     * 处理 SpringMVC 请求方法不正确
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public R<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.warn("request method not supported, ", e);
        return R.failed(BaseErrorCode.METHOD_NOT_SUPPORTED);
    }

    /**
     * 处理业务异常
     */
    @ExceptionHandler(value = BaseException.class)
    public R<?> handleBaseException(BaseException e) {
        log.error("[BaseExceptionHandler], cause: {}", e.getCause().getClass().getSimpleName(), e);
        return R.failed(BaseErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }

    /**
     * 处理系统异常，兜底处理所有的一切
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<?> handleException(Throwable e) throws Throwable {
        if (e instanceof AccessDeniedException) {
            throw e; // 不处理 Spring Security 权限不足的异常
        }
        log.error("[defaultExceptionHandler]", e);
        return R.failed(BaseErrorCode.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
    }

}
