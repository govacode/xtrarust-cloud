package com.govacode.designpatterns.creational.singleton;

/**
 * 基于类初始化的延迟加载解决方案（线程安全 推荐使用）
 * 在JVM加载StaticInnerClassSingleton外部类时不会加载内部类从而也不会初始化INSTANCE
 * 只有在第一次调用getInstance方法才会触发INSTANCE初始化，并且JVM自动会保证初始化线程安全
 *
 * @author gova
 */
public class StaticInnerClassSingleton {

    private StaticInnerClassSingleton() {
    }

    public static StaticInnerClassSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final StaticInnerClassSingleton INSTANCE = new StaticInnerClassSingleton();
    }
}
