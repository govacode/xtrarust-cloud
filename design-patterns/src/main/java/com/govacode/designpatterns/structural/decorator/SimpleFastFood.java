package com.govacode.designpatterns.structural.decorator;

import java.math.BigDecimal;

public class SimpleFastFood implements FastFood {

    private final String desc;

    private final BigDecimal price;

    public SimpleFastFood(String desc, BigDecimal price) {
        this.desc = desc;
        this.price = price;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @Override
    public BigDecimal getPrice() {
        return this.price;
    }
}
