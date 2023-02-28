package com.hyphenate.easemob.imlibs.cache;

import java.util.concurrent.TimeUnit;

public class OnlineCache extends BaseExpireMap<String, Boolean>{

    private static OnlineCache instance = null;
    static long expTime = 0L;
    static TimeUnit unit = null;

    OnlineCache(long expTime, TimeUnit unit) {
        super(expTime, unit);
    }

    public synchronized static void init(long expTime, TimeUnit unit) {
        OnlineCache.expTime = expTime;
        OnlineCache.unit = unit;
        if (instance == null) {
            instance = new OnlineCache(expTime, unit);
        }
    }

    public synchronized static OnlineCache getInstance() {
        if (instance == null) {
            if (unit == null) {
                throw new IllegalArgumentException("please call init at first");
            }
            instance = new OnlineCache(expTime, unit);
        }
        return instance;
    }

    /**
     * 过期事件
     * @param key
     * @param val
     */
    @Override
    protected void baseExpireEvent(String key, Boolean val) {
        remove(key);
    }



}
