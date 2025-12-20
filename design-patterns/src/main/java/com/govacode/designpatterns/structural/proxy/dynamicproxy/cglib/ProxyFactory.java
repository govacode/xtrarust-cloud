package com.govacode.designpatterns.structural.proxy.dynamicproxy.cglib;

import com.govacode.designpatterns.structural.proxy.staticproxy.TrainStation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class ProxyFactory implements MethodInterceptor {

    private final Object obj;

    public ProxyFactory(Object obj) {
        this.obj = obj;
    }

    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        log.info("代理类: {}", proxy.getClass());
        log.info("CGLIB代理增强");
        Object ret = method.invoke(obj, args);
        log.info("CGLIB代理执行结果: {}", ret);
        return ret;
    }

    public static void main(String[] args) {
        ((TrainStation) new ProxyFactory(new TrainStation()).getProxy()).sell(10);
    }
}
