package com.govacode.designpatterns.creational.singleton;

/**
 * 双重检查锁单例（线程安全 推荐使用）
 *
 * @author gova
 */
public class ThreadSafeDoubleCheckedLockSingleton {

    // volatile关键字两大作用：1.保证可见性2.禁止指令重排 这里的作用是禁止指令重排
    private static volatile ThreadSafeDoubleCheckedLockSingleton instance;

    private ThreadSafeDoubleCheckedLockSingleton() {
    }

    public static ThreadSafeDoubleCheckedLockSingleton getInstance() {
        if (instance == null) {
            synchronized (ThreadSafeDoubleCheckedLockSingleton.class) {
                if (instance == null) {
                    instance = new ThreadSafeDoubleCheckedLockSingleton();
                }
            }
        }
        return instance;
    }
}
