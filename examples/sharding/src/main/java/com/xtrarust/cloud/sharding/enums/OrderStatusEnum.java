package com.xtrarust.cloud.sharding.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    PENDING_PAYMENT("PENDING_PAYMENT", "待付款"),
    PAID("PAID", "已付款"),
    DONE("DONE", "完成");

    @EnumValue
    private final String code;

    private final String desc;
}
