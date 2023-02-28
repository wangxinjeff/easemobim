package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.easemob.imlibs.cache.OnlineCache;
import com.hyphenate.easemob.imlibs.cache.OnlineCacheFriends;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class EventOnLineOffLineNotify {

    private Map<String, Boolean> statusMap = new HashMap<>();

    public EventOnLineOffLineNotify(Map<String, String> userStatus){
        try {
            String imUserStr = userStatus.get("jid");
            String imStatusStr = userStatus.get("status");
            if (imUserStr != null && imStatusStr != null) {
                boolean status = "online".equalsIgnoreCase(imStatusStr);
                OnlineCacheFriends.getInstance().set(imUserStr, status);
                OnlineCache.getInstance().put(imUserStr, status);
            } else {
                String imUsers = userStatus.get("jids");
                String imStatus = userStatus.get("status");
                JSONArray imUserJson = new JSONArray(imUsers);
                JSONArray imStatusJson = new JSONArray(imStatus);
                if (imUserJson.length() == imStatusJson.length()) {
                    for (int i = 0; i < imUserJson.length(); i++) {
                        imUserStr = imUserJson.optString(i);
                        imStatusStr = imStatusJson.optString(i);
                        if (imUserStr != null && imStatusStr != null) {
                            boolean status =  "online".equalsIgnoreCase(imStatusStr);
                            statusMap.put(imUserStr, status);
                            OnlineCacheFriends.getInstance().set(imUserStr, status);
                            OnlineCache.getInstance().put(imUserStr, status);
                        }
                    }
                }

            }
        } catch (JSONException e) {
            MPLog.e("EventOnLineOffLineQuery", "userStatus:" + userStatus.toString());
        }
    }

    public Map<String, Boolean> getUserStatus() {
        return statusMap;
    }
}
