package com.lagou.client;

import com.lagou.beans.ServiceBean;
import com.lagou.beans.ServiceContainer;
import com.lagou.rpc.*;
import com.lagou.zk.ZKUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcConsumer {

    //创建线程池对象
    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static ArrayList<UserClientHandler> userClientHandlers;

    private static Random random = new Random();

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    //1.创建一个代理对象 providerName：UserService#sayHello are you ok?
    public Object createProxy(final Class<?> serviceClass) {
        //借助JDK动态代理生成代理对象
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{serviceClass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //（1）调用初始化netty客户端的方法
                if (userClientHandlers == null) {
                    userClientHandlers = new ArrayList<UserClientHandler>();
                    // 服务发现
                    ZKUtils.findService();
                    initClient();
                }
                atomicInteger.incrementAndGet();
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setRequestId(String.valueOf(System.currentTimeMillis()));
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setClassName(serviceClass.getName());
                rpcRequest.setParameters(args);
                Class<?>[] parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }
                rpcRequest.setParameterTypes(parameterTypes);
                System.out.println("request: " + rpcRequest);
                // 请求负载均衡
                UserClientHandler handler = userClientHandlers.get(atomicInteger.intValue() % userClientHandlers.size());
                handler.setRpcRequest(rpcRequest);
                // 去服务端请求数据
                return executor.submit(handler).get();
            }
        });
    }


    //2.初始化netty客户端
    public static void initClient() throws InterruptedException {
        Map<String, List<ServiceBean>> serivces = ServiceContainer.getSerivces();
        List<ServiceBean> serviceBeans = serivces.get(ZKUtils.serviceName);

        for (ServiceBean serviceBean : serviceBeans) {
            final UserClientHandler handler = new UserClientHandler();
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new RpcJsonEncoder(RpcRequest.class, new JSONSerializer()));
                            pipeline.addLast(new RpcJsonDecoder(RpcResponse.class, new JSONSerializer()));
                            pipeline.addLast(handler);
                        }
                    });
            userClientHandlers.add(handler);
            bootstrap.connect(serviceBean.getHost(), serviceBean.getPort()).sync();
            serviceBean.setContext(handler.getContext());
        }


    }


}
