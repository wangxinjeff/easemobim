package com.hyphenate.easemob.im.mp.location;

import com.baidu.mapapi.model.LatLng;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.eventbus.Subscribe;
import com.hyphenate.eventbus.ThreadMode;
import com.hyphenate.easemob.imlibs.mp.events.EventLocChanged;
import com.hyphenate.easemob.imlibs.mp.events.EventLocNotify;
import com.hyphenate.easemob.imlibs.mp.events.EventLocUserAdded;
import com.hyphenate.easemob.imlibs.mp.events.EventLocUserRemoved;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LatLngManager {

    private static LatLngManager sInstance;

    public static LatLngManager getInstance() {
        if (sInstance == null) {
            synchronized (LatLngManager.class) {
                if (sInstance == null) {
                    sInstance = new LatLngManager();
                }
            }
        }
        return sInstance;
    }

//    private Map<String, LatLngEntity> latLngEntityMap = Collections.synchronizedMap(new HashMap<>());

    private Map<String, Map<String, LatLngEntity>> latLngEntities = Collections.synchronizedMap(new HashMap<>());



    private LatLngManager(){

    }

    private static final int INTERVAL_TIME_SECOND = 30;// 单位: 秒
    private static final int INTERVAL_TIME = INTERVAL_TIME_SECOND * 1000; // 单位: 毫秒

    private String toChatUsername;
    private int chatType;

    public void setUsernameAndChatType(String to, int chatType) {
        this.toChatUsername = to;
        this.chatType = chatType;
        MPEventBus.getDefault().register(this);
    }

    public void unRegister(){
        MPEventBus.getDefault().unregister(this);
    }

    public int getSize(){
        if (toChatUsername == null) {
            return 0;
        }
        Map<String, LatLngEntity> item = latLngEntities.get(toChatUsername);
        if (item == null) {
            latLngEntities.remove(toChatUsername);
            return 0;
        }
        return item.size();
    }

    public Set<String> getUsernames(){
        if (toChatUsername == null) {
            return new HashSet<>();
        }
        Map<String, LatLngEntity> item = latLngEntities.get(toChatUsername);
        if (item == null) {
            latLngEntities.remove(toChatUsername);
            return new HashSet<>();
        }
        return item.keySet();
    }

    public List<LatLngEntity> getRealLatLng() {
        List<LatLngEntity>  list = new ArrayList<>();
        Map<String, LatLngEntity> itemMap;
        if (!latLngEntities.containsKey(toChatUsername)) {
            itemMap = new HashMap<>();
        } else {
            itemMap = latLngEntities.get(toChatUsername);
        }
        Iterator<String> iterator = itemMap.keySet().iterator();
        long currTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            String from = iterator.next();
            LatLngEntity item = itemMap.get(from);
            if (item == null) {
                continue;
            }
            if (currTime - item.timestamp > INTERVAL_TIME) {
                itemMap.remove(from);
                sendPostNotify();
            } else {
                list.add(item);
            }
        }
        return list;
    }

    public void addUser(String to, String username, double lat, double lng, float radius, float direction) {
        synchronized (latLngEntities) {
            Map<String, LatLngEntity> itemMap;
            if (!latLngEntities.containsKey(to)) {
                itemMap = new HashMap<>();
            } else {
                itemMap = latLngEntities.get(to);
            }
            LatLngEntity entty = new LatLngEntity(username, lat, lng, radius, direction, System.currentTimeMillis());
            itemMap.put(username, entty);
            latLngEntities.put(to, itemMap);
        }
        if (to.equals(toChatUsername)) {
            sendPostNotify();
        }
    }


    public void removeUser(String to, String username) {
        synchronized (latLngEntities) {
            Map<String, LatLngEntity> itemMap;
            if (!latLngEntities.containsKey(to)) {
                itemMap = new HashMap<>();
            } else {
                itemMap = latLngEntities.get(to);
            }
            if (itemMap.containsKey(username)) {
                itemMap.remove(username);
            }
        }
        if (to.equals(toChatUsername)) {
            sendPostNotify();
        }
    }

    private void sendPostNotify(){
        MPEventBus.getDefault().post(new EventLocNotify());
    }

//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    public void onLocUserAdded(EventLocUserAdded event) {
//        addUser(event.getTo(), event.getUsername(), 0, 0);
//    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onLocUserRemoved(EventLocUserRemoved event) {
        String to;
        if (event.getChatType() == Constant.CHATTYPE_GROUP) {
            to = event.getTo();
        } else {
            to = event.getFrom();
        }
        removeUser(to, event.getFrom());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onLocReceived(EventLocChanged event) {
        String to;
        if (event.getChatType() == Constant.CHATTYPE_GROUP) {
            to = event.getTo();
        } else {
            to = event.getFrom();
        }
        double lat = event.getLat();
        double lng = event.getLng();
        float radius = event.getRadius();
        float direction = event.getDirection();
        addUser(to, event.getFrom(), lat, lng, radius, direction);
    }


    public static class LatLngEntity {
        private String username;
        private double lat;
        private double lng;
        private float radius;
        private float direction;
        private long timestamp;

        public LatLngEntity(String username, double lat, double lng, float radius, float direction, long timestamp) {
            this.username = username;
            this.lat = lat;
            this.lng = lng;
            this.radius = radius;
            this.direction = direction;
            this.timestamp = timestamp;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public float getDirection() {
            return direction;
        }

        public void setDirection(float direction) {
            this.direction = direction;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
