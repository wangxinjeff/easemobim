package com.hyphenate.easemob.im.mp.location;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RTLocationManager {

    private static RTLocationManager sInstance = new RTLocationManager();

    public static RTLocationManager getInstance() {
        return sInstance;
    }

    private List<LocationListener> mListeners = Collections.synchronizedList(new ArrayList<>());

    public void addListener(LocationListener listener) {
        synchronized (mListeners) {
            if (!mListeners.contains(listener)) {
                mListeners.add(listener);
            }
        }
    }

    public void removeListener(LocationListener listener) {
        synchronized (mListeners) {
            if (mListeners.contains(listener)) {
                mListeners.remove(listener);
            }
        }
    }

    public void notifyListener(double lat, double lng, float radius, float direction) {
        synchronized (mListeners) {
            for (LocationListener listener : mListeners) {
                if (listener != null) {
                    listener.onPositionChanged(lat, lng, radius, direction);
                }
            }
        }
    }

    public static interface LocationListener {
        void onPositionChanged(double lat, double lng, float radius, float direction);
    }

}
