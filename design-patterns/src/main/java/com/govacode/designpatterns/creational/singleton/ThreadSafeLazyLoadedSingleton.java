package com.govacode.designpatterns.creational.singleton;

/**
 * 线程安全懒汉式单例（通过同步锁实现）
 * 存在问题：getInstance方法只在初始化时需要同步，后续获取单例不需要同步，效率低下
 * 改进：双重检查锁
 *
 * @author gova
 * @see DoubleCheckedLockSingleton
 * @see ThreadSafeDoubleCheckedLockSingleton
 */
public class ThreadSafeLazyLoadedSingleton {

    private static ThreadSafeLazyLoadedSingleton instance = null;

    private ThreadSafeLazyLoadedSingleton() {
    }

    public synchronized static ThreadSafeLazyLoadedSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeLazyLoadedSingleton();
        }
        return instance;
    }
}
