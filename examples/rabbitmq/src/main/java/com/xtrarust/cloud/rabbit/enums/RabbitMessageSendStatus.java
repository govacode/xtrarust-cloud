package com.xtrarust.cloud.rabbit.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RabbitMessageSendStatus {

    WAITING_SEND(0, "待发送"),
    SEND_SUCCESS(1, "已发送"),
    RETURNED(2, "未正确路由"),
    SEND_FAILED(3, "发送失败");

    private final Integer status;

    private final String desc;
}
