package com.findsomething.rpc.consumer.rpc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.findsomething.rpc.common.entity.Request;
import com.findsomething.rpc.common.entity.Response;
import com.findsomething.rpc.consumer.server.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/** @author link */
@Component
public class RpcFactory<T> implements InvocationHandler {

    @Autowired private NettyClient client;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        request.setRequestId(UUID.randomUUID().toString());

        Object result = client.send(request);
        Class<?> returnType = method.getReturnType();

        Response response = JSON.parseObject(result.toString(), Response.class);
        if (response.getCode() == -1) {
            throw new Exception(response.getError());
        }
        // 返回类型为原始类型或者为String类型的时候
        if (returnType.isPrimitive() || String.class.isAssignableFrom(returnType)) {
            return response.getData();
        }
        // 转化为集合类
        if (Collection.class.isAssignableFrom(returnType)) {
            return JSONArray.parseArray(response.getData().toString(), Object.class);
        }
        // 转化为map集合
        if (Map.class.isAssignableFrom(returnType)) {
            return JSON.parseObject(response.getData().toString(), Map.class);
        }
        Object data = response.getData();
        return JSONObject.parseObject(data.toString(), returnType);

    }
}
