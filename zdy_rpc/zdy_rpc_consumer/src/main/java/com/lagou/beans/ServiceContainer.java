package com.lagou.beans;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author shengx
 * @date 2020/4/21 22:14
 */
public class ServiceContainer {
    private static Map<String, List<ServiceBean>> serivces = new ConcurrentHashMap<String, List<ServiceBean>>();

    public static Map<String, List<ServiceBean>> getSerivces() {
        return serivces;
    }

    public static void setSerivces(Map<String, List<ServiceBean>> serivces) {
        ServiceContainer.serivces = serivces;
    }
}
