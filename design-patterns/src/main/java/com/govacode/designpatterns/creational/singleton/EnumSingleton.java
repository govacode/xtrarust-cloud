package com.govacode.designpatterns.creational.singleton;

/**
 * 枚举单例（Effective Java推荐使用的单例模式 由JVM保证线程安全且不会被反射和序列化破坏）
 *
 * @author gova
 */
public enum EnumSingleton {

    INSTANCE;
}
