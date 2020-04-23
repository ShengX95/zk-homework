package com.lagou.zk;

import com.lagou.beans.ServiceBean;
import com.lagou.beans.ServiceContainer;
import com.lagou.client.RpcConsumer;
import com.lagou.client.UserClientHandler;
import com.lagou.rpc.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.I0Itec.zkclient.ZkClient;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shengx
 * @date 2020/4/21 20:36
 */
public class ZKUtils {
    private static ZkClient zkClient;
    public final static String serviceName = "myService";
    public static void findService() throws Exception {
        zkClient = new ZkClient("192.168.79.100:2181,192.168.79.110:2181,192.168.79.120:2181", 5000);
        String servicePath = "/service/" + serviceName;
        List<String> children = zkClient.getChildren("/service/" + serviceName);
        zkClient.subscribeChildChanges(servicePath, new ServiceWatcher());
        List<ServiceBean> beans = new ArrayList<ServiceBean>();
        for (String sd : children) {
            ServiceBean bean = (ServiceBean)zkClient.readData(servicePath + "/" + sd);
            beans.add(bean);
        }
        ServiceContainer.getSerivces().put(serviceName, beans);
        System.out.println("服务发现：" + ServiceContainer.getSerivces());
    }

    public static void updateService(String s, List<String> list) throws InterruptedException {
        List<ServiceBean> beans = new ArrayList<ServiceBean>();
        for (String sd : list) {
            ServiceBean bean = (ServiceBean)zkClient.readData(s + "/" + sd);
            beans.add(bean);
        }
        // 下线机器
        for (ServiceBean serviceBean : ServiceContainer.getSerivces().get(serviceName)) {
            if(!beans.contains(serviceBean)){
                serviceBean.getContext().close();
                System.out.println(RpcConsumer.userClientHandlers.remove(serviceBean));
                System.out.println("下线机器：" + serviceBean);
            }
        }
        System.out.println(RpcConsumer.userClientHandlers);
        // 上线新机器
        for (ServiceBean bean : beans) {
            if(!ServiceContainer.getSerivces().get(serviceName).contains(bean)){
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
                RpcConsumer.userClientHandlers.add(handler);
                bootstrap.connect(bean.getHost(), bean.getPort()).sync();
                bean.setContext(handler.getContext());
                System.out.println("上线机器：" + bean);
            }
        }

        if (beans.size() > 0) {
            ServiceContainer.getSerivces().put(serviceName, beans);
        }else {
            ServiceContainer.getSerivces().remove(serviceName);
        }

        System.out.println("服务器列表变动：" + ServiceContainer.getSerivces());

    }

    public static void main(String[] args) throws InterruptedException {
        try {
            ZKUtils.findService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(Integer.MAX_VALUE);
    }
}
