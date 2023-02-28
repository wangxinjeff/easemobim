package com.hyphenate.easemob.easeui.domain;

import androidx.annotation.NonNull;

/**
 * 群信息
 */
public class GroupBean {

    private int groupId;
    @NonNull
    private String imGroupId;
    private String nick;
    private String avatar;
    private long createTime;
    private String type;
    private int isDisturb;
    private boolean isCluster;

    public GroupBean(int groupId, String imGroupId, String nick, String avatar, long createTime, String type) {
        this.groupId = groupId;
        this.imGroupId = imGroupId;
        this.nick = nick;
        this.avatar = avatar;
        this.createTime = createTime;
        this.type = type;
    }

    public GroupBean(int groupId, String imGroupId, int isDisturb) {
        this.groupId = groupId;
        this.imGroupId = imGroupId;
        this.isDisturb = isDisturb;
    }

    public GroupBean(int groupId, String imGroupId, String nick, String avatar, long createTime) {
        this.groupId = groupId;
        this.imGroupId = imGroupId;
        this.nick = nick;
        this.avatar = avatar;
        this.createTime = createTime;
        this.type = "NORMAL";
    }

    public GroupBean(int groupId, String imGroupId, String nick, String avatar) {
        this.groupId = groupId;
        this.imGroupId = imGroupId;
        this.nick = nick;
        this.avatar = avatar;
        this.createTime = System.currentTimeMillis();
        this.type = "NORMAL";
    }


    //    public GroupBean(String imGroupId, String nick, String avatar) {
//        this.imGroupId = imGroupId;
//        this.nick = nick;
//        this.avatar = avatar;
//        this.type = "NORMAL";
//    }

//    public GroupBean(String imGroupId, String nick, String avatar, long createTime, String type) {
//        this.imGroupId = imGroupId;
//        this.nick = nick;
//        this.avatar = avatar;
//        this.createTime = createTime;
//        this.type = type;
//    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImGroupId() {
        return imGroupId;
    }

    public void setImGroupId(String imGroupId) {
        this.imGroupId = imGroupId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getIsDisturb() {
        return isDisturb;
    }

    public void setIsDisturb(int isDisturb) {
        this.isDisturb = isDisturb;
    }

    public boolean isCluster() {
        return isCluster;
    }

    public void setCluster(boolean cluster) {
        isCluster = cluster;
    }
}
