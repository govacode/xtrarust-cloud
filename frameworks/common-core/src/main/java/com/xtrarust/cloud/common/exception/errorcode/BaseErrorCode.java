package com.xtrarust.cloud.common.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 基础错误码定义
 */
@Getter
@AllArgsConstructor
public enum BaseErrorCode implements IErrorCode {

    SUCCESS("200", "OK"),
    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Not Found"),
    METHOD_NOT_SUPPORTED("405", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error");

    private final String code;

    private final String message;
}
