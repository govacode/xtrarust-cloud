package com.govacode.designpatterns.behavoioral.mediator;

/**
 * 中介者模式测试
 *
 * @author gova
 * @see java.util.concurrent.Executor#execute(Runnable)
 * @see java.util.concurrent.ExecutorService#submit(Runnable)
 */
public class Client {

    public static void main(String[] args) {
        Mediator mediator = new ConcreteMediator();
        ConcreteColleague colleague1 = new ConcreteColleague("c1");
        mediator.register(colleague1);
        ConcreteColleague colleague2 = new ConcreteColleague("c2");
        mediator.register(colleague2);
        ConcreteColleague colleague3 = new ConcreteColleague("c3");
        mediator.register(colleague3);

        colleague1.send("hello everyone");
    }
}
