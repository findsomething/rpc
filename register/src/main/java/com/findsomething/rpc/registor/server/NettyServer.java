package com.findsomething.rpc.registor.server;

//import io.netty.channel.EventLoopGroup;
//import io.netty.channel.nio.NioEventLoopGroup;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
//import javax.imageio.spi.ServiceRegistry;
//import java.util.HashMap;
//import java.util.Map;


/**
 * @author link
 */
//@Component
//public class NettyServer implements ApplicationContextAware, InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
//
//    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
//    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(4);
//
//    private Map<String, Object> serviceMap = new HashMap<>();
//
//    @Value("${rpc.server.address}")
//    private String serviceAddress;
//
////    @Autowired
////    private ServiceRegistry registry;
//
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(RpcS)
//    }
//}
