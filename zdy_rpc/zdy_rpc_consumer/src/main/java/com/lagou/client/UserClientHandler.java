package com.lagou.client;

import com.lagou.rpc.RpcRequest;
import com.lagou.rpc.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable<Object> {
    private ChannelHandlerContext context;
    private RpcRequest rpcRequest;

    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }

    /**
     * 收到服务端数据，唤醒等待线程
     */
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        RpcResponse rpcResponse = (RpcResponse) msg;
        System.out.println("channel read: " + rpcResponse);
        GuardObject.fireEvent(rpcResponse.getRequestId(), rpcResponse);
    }

    public Object call() throws Exception {
        context.writeAndFlush(rpcRequest);
        GuardObject guardObject = GuardObject.create(rpcRequest.getRequestId());
        Object obj = null;
        if((obj = guardObject.get()) != null){
            return ((RpcResponse)obj).getResult();
        }
        return null;
    }

    public void setRpcRequest(RpcRequest rpcRequest) {
        this.rpcRequest = rpcRequest;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public void setContext(ChannelHandlerContext context) {
        this.context = context;
    }
}
