package com.findsomething.rpc.consumer.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/** @author link */
public class CommonProxy implements InvocationHandler {

    private Object obj;

    private static Logger logger = LoggerFactory.getLogger(CommonProxy.class);

    public CommonProxy(Object obj) {
        this.obj = obj;
    }

    public static Object getProxy(Object obj) {
        return Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                obj.getClass().getInterfaces(),
                new CommonProxy(obj));
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = new Object();
        return result;
    }
}
