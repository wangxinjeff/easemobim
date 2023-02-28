package com.hyphenate.easemob.im.officeautomation.domain;

public class ReferenceMsgEntity {
    private int id;

    private String referenceMsgId;

    private String realMsgId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReferenceMsgId() {
        return referenceMsgId;
    }

    public void setReferenceMsgId(String referenceMsgId) {
        this.referenceMsgId = referenceMsgId;
    }

    public String getRealMsgId() {
        return realMsgId;
    }

    public void setRealMsgId(String realMsgId) {
        this.realMsgId = realMsgId;
    }
}
