package com.govacode.designpatterns.creational.singleton;

/**
 * 懒汉式（线程不安全）
 *
 * @author gova
 * @see ThreadSafeLazyLoadedSingleton
 */
public class LazyLoadedSingleton {

    private static LazyLoadedSingleton instance = null;

    private LazyLoadedSingleton() {
    }

    public static LazyLoadedSingleton getInstance() {
        if (instance == null) {
            instance = new LazyLoadedSingleton();
        }
        return instance;
    }
}
