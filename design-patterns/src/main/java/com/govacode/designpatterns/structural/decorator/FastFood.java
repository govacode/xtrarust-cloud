package com.govacode.designpatterns.structural.decorator;

import java.math.BigDecimal;

/**
 * 快餐接口
 *
 * @author gova
 */
public interface FastFood {

    // 获取描述
    String getDesc();

    // 获取价格
    BigDecimal getPrice();
}
