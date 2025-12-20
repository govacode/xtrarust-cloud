package com.xtrarust.cloud.auth.common.enums;

import com.xtrarust.cloud.common.exception.errorcode.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityErrorCode implements IErrorCode {

    UNAUTHORIZED("401", "unauthorized"),
    FORBIDDEN("403", "forbidden"),
    ;

    private final String code;

    private final String message;
}
