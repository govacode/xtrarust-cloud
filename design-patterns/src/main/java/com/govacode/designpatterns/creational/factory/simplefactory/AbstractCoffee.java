package com.govacode.designpatterns.creational.factory.simplefactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCoffee {

    private static final Logger log = LoggerFactory.getLogger(AbstractCoffee.class);

    public abstract String getName();

    public void addSugar() {
        log.info("add sugar...");
    }

    public void addMilk() {
        log.info("add milk...");
    }
}
