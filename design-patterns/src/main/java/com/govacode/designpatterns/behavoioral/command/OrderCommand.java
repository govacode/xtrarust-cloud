package com.govacode.designpatterns.behavoioral.command;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 点餐命令
 *
 * @author gova
 */
@Slf4j
public class OrderCommand implements Command {

    // 厨师 命令接收者对象 receiver
    private final Chef chef;

    private final Order order;

    public OrderCommand(Chef chef, Order order) {
        this.chef = chef;
        this.order = order;
    }

    @Override
    public void execute() {
        log.info("{}桌的订单", order.getDiningTable());
        Map<String, Integer> food = order.getFood();
        if (food == null || food.isEmpty()) {
            return;
        }
        log.info("开始制作");
        food.forEach(chef::makeFood);
        try {
            // 模拟制作
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("{}桌的菜肴制作完毕", order.getDiningTable());
    }
}
