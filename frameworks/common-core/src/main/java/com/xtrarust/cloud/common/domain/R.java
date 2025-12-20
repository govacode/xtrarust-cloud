package com.xtrarust.cloud.common.domain;

import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xtrarust.cloud.common.exception.errorcode.IErrorCode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static com.xtrarust.cloud.common.exception.errorcode.BaseErrorCode.SUCCESS;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 */
@Data
@Accessors(chain = true)
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 返回码
     */
    private String code;
    /**
     * 返回消息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;

    public static R<Void> ok() {
        return new R<Void>().setCode(SUCCESS.getCode()).setMessage(SUCCESS.getMessage());
    }

    public static <T> R<T> ok(T data) {
        return new R<T>().setCode(SUCCESS.getCode()).setMessage(SUCCESS.getMessage()).setData(data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<T>().setCode(SUCCESS.getCode()).setMessage(Objects.toString(message, SUCCESS.getMessage())).setData(data);
    }

    public static <T> R<T> failed(IErrorCode errorCode) {
        Assert.isTrue(errorCode != null && errorCode != SUCCESS);
        return new R<T>().setCode(errorCode.getCode()).setMessage(errorCode.getMessage());
    }

    public static <T> R<T> failed(String code, String message) {
        return new R<T>().setCode(code).setMessage(message);
    }

    @JsonIgnore
    public boolean isSuccess() {
        return SUCCESS.getCode().equals(code);
    }

    @JsonIgnore
    public boolean isError() {
        return !isSuccess();
    }

}
