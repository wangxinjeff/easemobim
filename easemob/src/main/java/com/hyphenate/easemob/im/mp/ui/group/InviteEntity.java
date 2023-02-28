package com.hyphenate.easemob.im.mp.ui.group;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.json.JSONObject;

public class InviteEntity implements MultiItemEntity {
    private long createTime;
    private int userId;
    private int userChatGroupId;
    private String userRealName;
    private String userAvatar;
    private String userGender;
    private int chatGroupId;
    private String chatGroupName;

    private int invitorId;
    private String invitorAvatar;
    private String invitorRealName;
    private String invitorGender;
    private String organizationFullName;


    public static InviteEntity fromJson(JSONObject jsonObj) {
        if (jsonObj != null) {
            InviteEntity entty = new InviteEntity();
            entty.createTime = jsonObj.optLong("createTime");
            entty.userId = jsonObj.optInt("userId");
            entty.userChatGroupId = jsonObj.optInt("userChatGroupId");
            entty.userRealName = jsonObj.optString("userRealName");
            entty.userAvatar = jsonObj.optString("userAvatar");
            entty.userGender = jsonObj.optString("userGender");
            entty.invitorId = jsonObj.optInt("invitorId");
            entty.invitorAvatar = jsonObj.optString("invitorAvatar");
            entty.invitorRealName = jsonObj.optString("invitorRealName");
            entty.invitorGender = jsonObj.optString("invitorGender");
            entty.organizationFullName = jsonObj.optString("organizationFullName");
            return entty;
        }
        return null;
    }

    public String getChatGroupName() {
        return chatGroupName;
    }

    public void setChatGroupName(String chatGroupName) {
        this.chatGroupName = chatGroupName;
    }

    public int getChatGroupId() {
        return chatGroupId;
    }

    public void setChatGroupId(int chatGroupId) {
        this.chatGroupId = chatGroupId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserChatGroupId() {
        return userChatGroupId;
    }

    public void setUserChatGroupId(int userChatGroupId) {
        this.userChatGroupId = userChatGroupId;
    }

    public String getUserRealName() {
        return userRealName;
    }

    public void setUserRealName(String userRealName) {
        this.userRealName = userRealName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public int getInvitorId() {
        return invitorId;
    }

    public void setInvitorId(int invitorId) {
        this.invitorId = invitorId;
    }

    public String getInvitorAvatar() {
        return invitorAvatar;
    }

    public void setInvitorAvatar(String invitorAvatar) {
        this.invitorAvatar = invitorAvatar;
    }

    public String getInvitorRealName() {
        return invitorRealName;
    }

    public void setInvitorRealName(String invitorRealName) {
        this.invitorRealName = invitorRealName;
    }

    public String getInvitorGender() {
        return invitorGender;
    }

    public void setInvitorGender(String invitorGender) {
        this.invitorGender = invitorGender;
    }

    public String getOrganizationFullName() {
        return organizationFullName;
    }

    public void setOrganizationFullName(String organizationFullName) {
        this.organizationFullName = organizationFullName;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
