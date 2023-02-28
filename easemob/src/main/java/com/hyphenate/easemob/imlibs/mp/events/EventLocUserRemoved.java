package com.hyphenate.easemob.imlibs.mp.events;

public class EventLocUserRemoved {
    private String from;
    private String to;
    private int chatType;

    public EventLocUserRemoved(String from, String to, int chatType) {
        this.from = from;
        this.to = to;
        this.chatType = chatType;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
