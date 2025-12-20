package com.govacode.designpatterns.structural.decorator;

import java.math.BigDecimal;

public class FastFoodDecorator implements FastFood {

    private static final int DEFAULT_COUNT = 1;

    private final FastFood fastFood;

    private final String desc;

    private final BigDecimal price;

    private final int count;

    public FastFoodDecorator(FastFood fastFood, String desc, BigDecimal price) {
        this.fastFood = fastFood;
        this.desc = desc;
        this.price = price;
        this.count = DEFAULT_COUNT;
    }

    public FastFoodDecorator(FastFood fastFood, String desc, BigDecimal price, int count) {
        this.fastFood = fastFood;
        this.desc = desc;
        this.price = price;
        this.count = count;
    }

    @Override
    public String getDesc() {
        return fastFood.getDesc() + "+" + count + "个" + this.desc;
    }

    @Override
    public BigDecimal getPrice() {
        return fastFood.getPrice().add(BigDecimal.valueOf(count).multiply(this.price));
    }

}
