package com.lagou.client;

import com.lagou.service.UserService;

public class ClientBootStrap {

    public static void main(String[] args) throws InterruptedException {
        RpcConsumer rpcConsumer = new RpcConsumer();
        UserService proxy = (UserService) rpcConsumer.createProxy(UserService.class);
        while (true){
            Thread.sleep(2000);
            System.out.println("result: " + proxy.sayHello("are you ok?"));
        }
    }
}
