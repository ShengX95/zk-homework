package com.lagou.zk;

import org.I0Itec.zkclient.IZkChildListener;

import java.util.List;

/**
 * @author shengx
 * @date 2020/4/21 21:59
 */
public class ServiceWatcher implements IZkChildListener {
    public void handleChildChange(String s, List<String> list) throws Exception {
        ZKUtils.updateService(s, list);
    }


}
