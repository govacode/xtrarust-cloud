package com.govacode.designpatterns.behavoioral.command;

import java.util.HashMap;
import java.util.Map;

/**
 * 点餐订单
 *
 * @author gova
 */
public class Order {

    /**
     * 餐桌号
     */
    private int diningTable;

    /**
     * 菜肴及份数
     */
    private Map<String, Integer> food;

    public Order(int diningTable) {
        this.diningTable = diningTable;
        this.food = new HashMap<>();
    }

    public void addFood(String name, int num) {
        food.put(name, num);
    }

    public int getDiningTable() {
        return diningTable;
    }

    public Map<String, Integer> getFood() {
        return food;
    }
}
