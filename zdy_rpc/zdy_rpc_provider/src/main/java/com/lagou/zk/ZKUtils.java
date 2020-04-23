package com.lagou.zk;

import com.lagou.beans.ServiceBean;
import org.I0Itec.zkclient.ZkClient;

/**
 * @author shengx
 * @date 2020/4/21 20:36
 */
public class ZKUtils {
    private static ZkClient zkClient;
    public void signUp(String host, int port, String serviceName) throws Exception {
        zkClient = new ZkClient("192.168.79.100:2181,192.168.79.110:2181,192.168.79.120:2181", 5000);
        ServiceBean serviceBean = new ServiceBean(host, port, serviceName);
        zkClient.createEphemeralSequential("/service/" + serviceName + "/id", serviceBean);
//        client.start();
//        System.out.println("Zookeeper session established.");
//        client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/" + serviceName + "/id", (host + ":" + port).getBytes());
    }

    public void close(){
        zkClient.close();
    }
}
