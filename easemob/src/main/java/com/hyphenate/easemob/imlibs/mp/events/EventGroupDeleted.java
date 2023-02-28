package com.hyphenate.easemob.imlibs.mp.events;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 28/11/2018
 */
public class EventGroupDeleted {

    private int groupId;
    private String imGroupId;

    public EventGroupDeleted() {

    }

    public EventGroupDeleted(int groupId, String imGroupId) {
        this.groupId = groupId;
        this.imGroupId = imGroupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getImGroupId() {
        return imGroupId;
    }

    public void setImGroupId(String imGroupId) {
        this.imGroupId = imGroupId;
    }
}
