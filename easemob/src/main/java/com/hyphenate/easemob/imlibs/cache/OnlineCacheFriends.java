package com.hyphenate.easemob.imlibs.cache;

import android.util.LruCache;

public class OnlineCacheFriends {

    private final LruCache<String, Boolean> userStatus = new LruCache<>(200);

    private static OnlineCacheFriends sInstance;

    public static OnlineCacheFriends getInstance() {
        synchronized (OnlineCacheFriends.class) {
            if (sInstance == null) {
                sInstance = new OnlineCacheFriends();
            }
        }
        return sInstance;
    }


    public void set(String imUser, boolean status) {
        //MPLog.e("###", "imUser:" + imUser + ",status:" + status);
        synchronized (userStatus) {
            userStatus.put(imUser, status);
        }
    }

    public Boolean get(String imUser) {
        synchronized (userStatus) {
            return userStatus.get(imUser);
        }
    }

    public Boolean remove(String imUser) {
        synchronized (userStatus) {
            return userStatus.remove(imUser);
        }
    }

}
