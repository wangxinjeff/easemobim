package com.hyphenate.easemob.imlibs.officeautomation.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MPSessionEntity {

    private int id;
    private long createTime;
    private long lastUpdateTime;
    private int tenantId;
    private int userId;
    private int toId;
    private String chatType;
    private boolean isTop;
    private long topTime;
    private String avatar;
    private String name;
    private boolean isDisturb;
    private String imId;

    public static MPSessionEntity create(JSONObject jsonObj) {
        if (jsonObj == null) {
            return null;
        }
        MPSessionEntity sessionEntity = new MPSessionEntity();
        sessionEntity.id = jsonObj.optInt("id");
        sessionEntity.createTime = jsonObj.optLong("createTime");
        sessionEntity.lastUpdateTime = jsonObj.optLong("lastUpdateTime");
        sessionEntity.tenantId = jsonObj.optInt("tenantId");
        sessionEntity.userId = jsonObj.optInt("userId");
        sessionEntity.toId = jsonObj.optInt("toId");
        sessionEntity.chatType = jsonObj.optString("chatType");
        sessionEntity.isTop = jsonObj.optBoolean("isTop");
        sessionEntity.topTime = jsonObj.optLong("topTime");
        JSONObject jsonChatGroup = jsonObj.optJSONObject("chatGroup");
        if (jsonChatGroup != null) {
            sessionEntity.name = jsonChatGroup.optString("name");
            sessionEntity.avatar = jsonChatGroup.optString("avatar");

            JSONObject jsonImGroup = jsonChatGroup.optJSONObject("imChatGroup");
            if (jsonImGroup != null) {
                sessionEntity.imId = jsonImGroup.optString("imChatGroupId");
            }
            sessionEntity.isDisturb = jsonChatGroup.optBoolean("disturb");
        }

        JSONObject jsonUser = jsonObj.optJSONObject("user");
        if (jsonUser != null) {
            sessionEntity.name = jsonUser.optString("realName");
            sessionEntity.avatar = jsonUser.optString("avatar");
            JSONObject jsonImUser = jsonUser.optJSONObject("imUser");
            if (jsonImUser != null) {
                sessionEntity.imId = jsonImUser.optString("imUsername");
            }
            sessionEntity.isDisturb = jsonUser.optBoolean("disturb");
        }
        return sessionEntity;
    }


    public static List<MPSessionEntity> create(JSONArray jsonArr) {
        List<MPSessionEntity> mpSessionEntities = new ArrayList<>();
        if (jsonArr == null || jsonArr.length() == 0) {
            return mpSessionEntities;
        }
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.optJSONObject(i);
            MPSessionEntity sessionEntity = MPSessionEntity.create(jsonObj);
            if (sessionEntity != null) {
                mpSessionEntities.add(sessionEntity);
            }
        }
        return mpSessionEntities;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public long getTopTime() {
        return topTime;
    }

    public void setTopTime(long topTime) {
        this.topTime = topTime;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDisturb() {
        return isDisturb;
    }

    public void setDisturb(boolean disturb) {
        isDisturb = disturb;
    }

    public String getImId() {
        return imId;
    }

    public void setImId(String imId) {
        this.imId = imId;
    }
}
