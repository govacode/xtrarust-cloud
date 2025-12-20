package com.govacode.designpatterns.creational.factory.simplefactory;

/**
 * 咖啡简单工厂类（简单工厂违背了开闭原则）
 *
 * @author gova
 */
public class SimpleCoffeeFactory {

    public static AbstractCoffee createCoffee(String type) {
        AbstractCoffee coffee = null;
        if ("latte".equals(type)) {
            coffee = new LatteCoffee();
        } else if ("cappuccino".equals(type)) {
            coffee = new CappuccinoCoffee();
        } else {
            throw new RuntimeException("unsupported coffee type: " + type);
        }
        return coffee;
    }
}
