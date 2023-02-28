package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.easemob.imlibs.cache.OnlineCache;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class EventOnLineOffLineQuery {

    private Map<String, Boolean> statusMap = new HashMap<>();

    public EventOnLineOffLineQuery(Map<String, String> userStatus) {
//        MPLog.e("EventOnLineOffLineQuery", "userStatus:" + userStatus);
        String imUsers = userStatus.get("jids");
        String imStatus = userStatus.get("status");
        try {
            JSONArray imUserJson = new JSONArray(imUsers);
            JSONArray imStatusJson = new JSONArray(imStatus);

            if (imUserJson != null && imStatusJson != null && imUserJson.length() == imStatusJson.length()) {
                for (int i = 0; i < imUserJson.length(); i++) {
                    String imUserStr = imUserJson.optString(i);
                    String imStatusStr = imStatusJson.optString(i);
                    if (imUserStr != null && imStatusStr != null) {
                        boolean sb = "online".equalsIgnoreCase(imStatusStr);
                        statusMap.put(imUserStr, sb);
                        OnlineCache.getInstance().put(imUserStr, sb);
                    }
                }
            }
        } catch (JSONException e) {
            MPLog.e("EventOnLineOffLineQuery", "imUsers:" + imUsers + ", imStatus:" + imStatus);
        }

    }

    public Map<String, Boolean> getUserStatus() {
        return statusMap;
    }

}
