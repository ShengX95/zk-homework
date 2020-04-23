package com.lagou.server;


import com.lagou.handler.UserServerHandler;
import com.lagou.rpc.*;
import com.lagou.utils.SpringUtils;
import com.lagou.zk.ZKUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.lagou")
public class ServerBootstrap implements ApplicationRunner {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(ServerBootstrap.class, args);
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        startServer("127.0.0.1",8991, "myService");
    }

    public void startServer(String hostName,int port, String serviceName) throws InterruptedException {

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        io.netty.bootstrap.ServerBootstrap serverBootstrap = new io.netty.bootstrap.ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new RpcJsonEncoder(RpcResponse.class, new JSONSerializer()));
                        pipeline.addLast(new RpcJsonDecoder(RpcRequest.class, new JSONSerializer()));
                        pipeline.addLast(SpringUtils.getBean(UserServerHandler.class));

                    }
                });

        ChannelFuture future = serverBootstrap.bind(hostName, port).sync();
        System.out.println("....Server is Start....");
        ZKUtils zkUtils = new ZKUtils();
        try {
            zkUtils.signUp(hostName, port, serviceName);
        } catch (Exception e) {
            e.printStackTrace();
            zkUtils.close();
        }
    }

}
