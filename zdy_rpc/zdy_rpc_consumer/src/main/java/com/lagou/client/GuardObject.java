package com.lagou.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author shengx
 * @date 2020/4/14 22:22
 */
public class GuardObject<T> {
    T obj;
    final Lock lock = new ReentrantLock();
    final Condition done = lock.newCondition();
    final int timeout = 5;
    final static Map<Object, GuardObject> gos = new ConcurrentHashMap<Object, GuardObject>();

    public static GuardObject create(Object key) {
        GuardObject go = new GuardObject();
        gos.put(key, go);
        return go;
    }

    public static <K, T> void fireEvent(K key, T obj) {
        GuardObject go = gos.remove(key);
        if (go != null) {
            go.onChanged(obj);
        }
    }

    T get() {
        lock.lock();
        try {
            //MESA管程推荐写法
            if (obj == null) {
                done.await(timeout, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            //返回非空的受保护对象
            return obj;
        }
    }

    //事件通知方法
    private void onChanged(T obj) {
        lock.lock();
        try {
            this.obj = obj;
            done.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
