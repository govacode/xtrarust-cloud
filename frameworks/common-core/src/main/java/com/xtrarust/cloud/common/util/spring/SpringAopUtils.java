package com.xtrarust.cloud.common.util.spring;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

/**
 * Spring AOP 工具类
 *
 * 参考波克尔 http://www.bubuko.com/infodetail-3471885.html 实现
 */
public class SpringAopUtils {

    /**
     * 获取aop代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 获取代理的目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象
     */
    public static Object getTarget(Object proxy) throws Exception {
        if (!AopUtils.isAopProxy(proxy)) {
            return proxy;
        }
        if (AopUtils.isJdkDynamicProxy(proxy)) {
            return getJdkDynamicProxyTargetObject(proxy);
        }
        return getCglibProxyTargetObject(proxy);
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        Object dynamicAdvisedInterceptor = BeanUtil.getFieldValue(proxy, "CGLIB$CALLBACK_0");
        AdvisedSupport advisedSupport = (AdvisedSupport) BeanUtil.getFieldValue(dynamicAdvisedInterceptor, "advised");
        return advisedSupport.getTargetSource().getTarget();
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        AopProxy aopProxy = (AopProxy) BeanUtil.getFieldValue(proxy, "h");
        AdvisedSupport advisedSupport = (AdvisedSupport) BeanUtil.getFieldValue(aopProxy, "advised");
        return advisedSupport.getTargetSource().getTarget();
    }

}
