package com.govacode.designpatterns.structural.proxy.dynamicproxy.jdk;

import com.govacode.designpatterns.structural.proxy.staticproxy.SellTickets;
import com.govacode.designpatterns.structural.proxy.staticproxy.TrainStation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

@Slf4j
public class ProxyFactory implements InvocationHandler {

    private final Object obj;

    public ProxyFactory(Object obj) {
        Objects.requireNonNull(obj, "obj can't be null");
        this.obj = obj;
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("代理类: {}", proxy.getClass());
        log.info("JDK代理增强");
        Object ret = method.invoke(obj, args);
        log.info("JDK代理执行结果: {}", ret);
        return ret;
    }

    public static void main(String[] args) {
        ProxyFactory proxyFactory = new ProxyFactory(new TrainStation());
        ((SellTickets) proxyFactory.getProxy()).sell(10);
    }
}
