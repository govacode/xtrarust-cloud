package com.govacode.designpatterns.behavoioral.command;

public class Client {

    public static void main(String[] args) {
        Waiter waiter = new Waiter();
        Chef chef = new Chef();
        Order order1 = new Order(55);
        order1.addFood("剁椒鱼头", 1);
        order1.addFood("干锅花菜", 1);

        Order order2 = new Order(60);
        order2.addFood("干锅兔", 1);
        order2.addFood("重庆烤鱼", 1);

        OrderCommand command1 = new OrderCommand(chef, order1);
        OrderCommand command2 = new OrderCommand(chef, order2);
        waiter.addCommand(command1);
        waiter.addCommand(command2);

        waiter.submitOrder();
    }
}
