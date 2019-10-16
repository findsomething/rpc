package com.findsomething.rpc.provider.server;

import com.alibaba.fastjson.JSON;
import com.findsomething.rpc.common.constants.RpcConstant;
import com.findsomething.rpc.common.entity.Request;
import com.findsomething.rpc.common.entity.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/** @author link */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final Map<String, Object> serviceMap;

    public NettyServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("客户端连接成功！" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("客户端断开连接！{}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 1.解析接收消息
        Request request = JSON.parseObject(msg.toString(), Request.class);

        if (RpcConstant.PING.equals(request.getMethodName())) {
            logger.info("客户端心跳信息..." + ctx.channel().remoteAddress());
            return;
        }
        logger.info("RPC客户端请求接口:" + request.getClassName() + " 方法名:" + request.getMethodName());
        // 2.通过反射，调用本地方法
        Response response = new Response();
        response.setRequestId(request.getRequestId());
        try {
            Object result = handle(request);
            response.setData(result);
        } catch (Throwable e) {
            logger.error("RPC ServerNode handle request error {}", e);
            response.setCode(-1);
            response.setError(e.toString());
        }
        ctx.writeAndFlush(response);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleStateEvent.ALL_IDLE_STATE_EVENT.state())) {
                logger.info("客户端已经超过60秒未读写数据，关闭连接.{}", ctx.channel().remoteAddress());
                ctx.channel().close();
                return;
            }
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info(cause.getMessage());
        ctx.close();
    }

    /**
     * 通过反射，执行本地方法
     *
     * @param request
     * @return
     * @throws Throwable
     */
    private Object handle(Request request) throws Throwable {
        String className = request.getClassName();
        Object serviceBean = serviceMap.get(className);
        if (null == serviceBean) {
            throw new Exception("未找到服务接口，请检查配置!:" + className + "#" + request.getMethodName());
        }
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, getParameters(parameterTypes, parameters));
    }

    /**
     * 解析获取参数列表
     *
     * @param parameterTypes
     * @param parameters
     * @return
     */
    private Object[] getParameters(Class<?>[] parameterTypes, Object[] parameters) {
        if (null == parameters || parameters.length == 0) {
            return parameters;
        }
        Object[] newParameters = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            newParameters[i] = JSON.parseObject(parameters[i].toString(), parameterTypes[i]);
        }
        return newParameters;
    }
}
