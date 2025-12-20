package com.govacode.designpatterns.creational.singleton;

/**
 * 双重检查锁单例（线程不安全）
 *
 * @author gova
 * @see ThreadSafeDoubleCheckedLockSingleton
 */
public class DoubleCheckedLockSingleton {

    private static DoubleCheckedLockSingleton singleton = null;

    private DoubleCheckedLockSingleton() {

    }

    public static DoubleCheckedLockSingleton getInstance() {
        if (singleton == null) {
            synchronized (DoubleCheckedLockSingleton.class) {
                if (singleton == null) {
                    // 存在指令重排序问题
                    singleton = new DoubleCheckedLockSingleton();
                }
            }
        }
        return singleton;
    }
}
