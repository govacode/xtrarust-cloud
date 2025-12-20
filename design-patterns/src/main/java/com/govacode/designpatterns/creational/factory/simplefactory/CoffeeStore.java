package com.govacode.designpatterns.creational.factory.simplefactory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CoffeeStore {

    public AbstractCoffee orderCoffee(String type) {
        AbstractCoffee coffee = SimpleCoffeeFactory.createCoffee(type);

        coffee.addSugar();
        coffee.addMilk();
        return coffee;
    }

    public static void main(String[] args) {
        CoffeeStore coffeeStore = new CoffeeStore();
        AbstractCoffee coffee = coffeeStore.orderCoffee("latte");
        log.info("coffee name: {}", coffee.getName());
    }
}
