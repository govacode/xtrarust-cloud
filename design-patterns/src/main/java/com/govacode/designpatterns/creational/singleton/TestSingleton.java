package com.govacode.designpatterns.creational.singleton;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

@Slf4j
public class TestSingleton {

    public static void main(String[] args) throws Exception {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        ThreadSafeDoubleCheckedLockSingleton singleton1 = threadPool.submit(new CreateSingletonTask()).get();
        ThreadSafeDoubleCheckedLockSingleton singleton2 = threadPool.submit(new CreateSingletonTask()).get();
        ThreadSafeDoubleCheckedLockSingleton singleton3 = threadPool.submit(new CreateSingletonTask()).get();
        ThreadSafeDoubleCheckedLockSingleton singleton4 = threadPool.submit(new CreateSingletonTask()).get();

        log.info("singleton1 == singleton2 && singleton2 == singleton3 && singleton3 == singleton4: {}",
                singleton1 == singleton2 && singleton2 == singleton3 && singleton3 == singleton4);

        threadPool.shutdown();

        // 反射破坏双重检查锁单例
        Class<ThreadSafeDoubleCheckedLockSingleton> clazz = ThreadSafeDoubleCheckedLockSingleton.class;
        Constructor<ThreadSafeDoubleCheckedLockSingleton> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);
        ThreadSafeDoubleCheckedLockSingleton singleton = constructor.newInstance();
        ThreadSafeDoubleCheckedLockSingleton anotherSingleton = constructor.newInstance();
        log.info("singleton == anotherSingleton: {}", singleton == anotherSingleton);

        // 饿汉单例：java.lang.Runtime
        Runtime runtime = Runtime.getRuntime();
        log.info("availableProcessors: {}, totalMemory: {}, maxMemory: {}, freeMemory: {}",
                runtime.availableProcessors(), runtime.totalMemory(), runtime.maxMemory(), runtime.freeMemory());
        Process process = runtime.exec("ifconfig");
        try (InputStream is = process.getInputStream()) {
            byte[] bytes = is.readAllBytes();
            log.info("ifconfig output: \n{}", new String(bytes, StandardCharsets.UTF_8));
        }

        // 饿汉单例：com.sun.org.apache.xml.internal.utils.XMLReaderManager
        // 枚举单例：com.google.common.base.Functions
    }

    static class CreateSingletonTask implements Callable<ThreadSafeDoubleCheckedLockSingleton> {

        @Override
        public ThreadSafeDoubleCheckedLockSingleton call() {
            return ThreadSafeDoubleCheckedLockSingleton.getInstance();
        }
    }

}
