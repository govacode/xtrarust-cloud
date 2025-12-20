package com.govacode.designpatterns.structural.facade;

/**
 * 外观模式客户端测试类
 *
 * @author fulgens
 */
public class Client {

    public static void main(String[] args) {
        DwarvenGoldmineFacade facade = new DwarvenGoldmineFacade();
        facade.startNewDay();
        facade.digOutGold();
        facade.endDay();
    }
}
