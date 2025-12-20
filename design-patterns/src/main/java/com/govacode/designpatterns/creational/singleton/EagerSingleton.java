package com.govacode.designpatterns.creational.singleton;

/**
 * 饿汉式单例（类加载即创建单例对象 线程安全但浪费内存）
 *
 * @author gova
 */
public class EagerSingleton {

    private static final EagerSingleton INSTANCE = new EagerSingleton();

//    private static final EagerSingleton instance;
//
//    static {
//        instance = new EagerSingleton();
//    }

    private EagerSingleton() {
    }

    public static EagerSingleton getInstance() {
        return INSTANCE;
    }
}
