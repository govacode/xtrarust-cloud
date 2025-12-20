package com.govacode.designpatterns.structural.decorator;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 装饰者模式
 * JDK中使用：
 * {@link java.io.BufferedWriter}、{@link java.io.BufferedReader}
 *
 * @author gova
 */
@Slf4j
public class Client {

    public static void main(String[] args) {
        // 炒面加蛋
        FastFood firedNoodles = new SimpleFastFood("炒面", BigDecimal.TEN);
        FastFood fastFood = new FastFoodDecorator(firedNoodles, "鸡蛋", BigDecimal.ONE, 2);
        log.info("{}价格:{}元", fastFood.getDesc(), fastFood.getPrice());
        // 炒面加蛋加培根
        fastFood = new FastFoodDecorator(fastFood, "培根", BigDecimal.valueOf(5L));
        log.info("{}价格:{}元", fastFood.getDesc(), fastFood.getPrice());
    }
}
