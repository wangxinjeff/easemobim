package com.hyphenate.easemob.imlibs.cache;


import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 实现延迟过期Map集合 支持自定义过期触发事件
 * @author liyuzhao
 * @param <K>
 * @param <V>
 */
public abstract class BaseExpireMap<K, V> {

    private long expTime = 0L;
    private TimeUnit unit = null;

    /**
     * 线程安全的map容器
     */
    ConcurrentHashMap<K, V> expireMap = null;
    /**
     * 控制过期时间
     */
    ConcurrentHashMap<K, Long> delayMap = null;

    /**
     * 将map提供给外部程序操作
     * @Title: getDataMap
     * @return
     */
    public ConcurrentHashMap<K, V> getDataMap() {
        return this.expireMap;
    }

    public BaseExpireMap(long expTime, TimeUnit unit) {
        expireMap = new ConcurrentHashMap<K, V>();
        delayMap = new ConcurrentHashMap<K, Long>();
        this.expTime = expTime;
        this.unit = unit;
        // 启动监听线程
        BaseExpireCheckTask task = new BaseExpireCheckTask(expireMap, delayMap) {
            @Override
            protected void expireEvent(K key, V val) {
                baseExpireEvent(key, val);
            }
        };
        task.setDaemon(false);
        task.start();

    }

    /**
     * 过期事件 子类实现
     * @param key
     * @param val
     */
    protected abstract void baseExpireEvent(K key, V val);

    public V put(K key, V val) {
        delayMap.put(key, getExpireTime());
        return expireMap.put(key, val);
    }

    public V remove(K key) {
        return expireMap.remove(key);
    }

    public V get(K key) {
        return expireMap.get(key);
    }

    private Long getExpireTime() {
        return unit.toMillis(expTime) + System.currentTimeMillis();
    }

    public static void main(String[] args) {
        System.out.println(TimeUnit.SECONDS.toMinutes(120));
        System.out.println(TimeUnit.MICROSECONDS.toMillis(120));
        System.out.println(TimeUnit.MILLISECONDS.toMillis(120));
    }

    /**
     * 扫描线程 定期移除过期元素并触发过期事件
     * @author liyuzhao
     */
    private abstract class BaseExpireCheckTask extends Thread {
        ConcurrentHashMap<K, Long> delayMap = null;
        ConcurrentHashMap<K, V> expireMap = null;

        public BaseExpireCheckTask(ConcurrentHashMap<K, V> expireMap, ConcurrentHashMap<K, Long> delayMap) {
            this.delayMap = delayMap;
            this.expireMap = expireMap;
        }

        protected abstract void expireEvent(K key, V val);

        public void run() {
            Iterator<K> it = null;
            K key = null;
            while(true) {
                if (delayMap != null && !delayMap.isEmpty()) {
                    it = delayMap.keySet().iterator();
                    while(it.hasNext()) {
                        key = it.next();
                        if(delayMap.get(key) <= System.currentTimeMillis()) { // 元素超时
                            // 触发回调
                            expireEvent(key, expireMap.get(key));
                            // 移除
                            it.remove();
                            expireMap.remove(key);
                            delayMap.remove(key);
                        }
                    }
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    MPLog.e("BaseExpire", e.getMessage());
                }
            }

        }

    }




}
