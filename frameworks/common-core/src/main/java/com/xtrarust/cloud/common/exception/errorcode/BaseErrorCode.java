package com.xtrarust.cloud.common.exception.errorcode;

/**
 * 基础错误码定义
 */
public enum BaseErrorCode implements IErrorCode {

    SUCCESS("success", ""),
    // ======================== 客户端错误段 A ========================
    // A01 保留 用户注册相关
    // A02 保留 用户登录相关
    // A03 保留 用户权限相关
    CLIENT_ERROR("A0001", "用户端错误"),
    MISSING_REQUEST_PARAMETER("A0401", "请求参数缺失：{}"),
    METHOD_ARGUMENT_TYPE_MISMATCH("A0402", "请求参数：{}类型错误"),
    REQUEST_PARAM_NOT_VALID("A0403", "请求参数不正确：{}"),
    NO_HANDLER_FOUND("A0404", "请求地址：{}不存在"),
    REQUEST_METHOD_NOT_SUPPORTED("A0405", "请求方法不正确"),
    TOO_MANY_REQUESTS("A0500", "请求过于频繁，请稍后重试"),

    // ======================== 服务端错误段 B ========================
    SERVICE_ERROR("B0001", "系统执行出错"),
    SERVICE_TIMEOUT_ERROR("B0100", "系统执行超时"),
    // ======================== 三方服务错误段 C ========================
    THIRD_PARTY_SERVICE_ERROR("C0001", "调用第三方服务出错"),
    MIDDLEWARE_ERROR("C0100", "中间件服务出错"),
    RPC_SERVICE_ERROR("C0110", "RPC 服务出错"),
    RPC_SERVICE_NOT_FOUND("C0111", "RPC 服务未找到"),
    RPC_SERVICE_NOT_REGISTERED("C0112", "RPC 服务未注册"),
    SERVICE_INTERFACE_NOT_EXIST("C0113", "接口不存在");

    private final String code;

    private final String message;

    BaseErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
