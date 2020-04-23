package com.lagou.server;

/**
 * @author shengx
 * @date 2020/4/21 21:35
 */
public class Starter {
    public static void main(String[] args) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.run(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
