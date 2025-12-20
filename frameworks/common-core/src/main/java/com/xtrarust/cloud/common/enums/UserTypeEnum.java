package com.xtrarust.cloud.common.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
@AllArgsConstructor
public enum UserTypeEnum {

    MEMBER(1, "会员"), // 面向 c 端，普通用户
    ADMIN(2, "管理员"); // 面向 b 端，管理后台

    private final Integer type;

    private final String name;

    public static UserTypeEnum valueOf(Integer type) {
        return ArrayUtil.firstMatch(userType -> userType.getType().equals(type), UserTypeEnum.values());
    }

}
