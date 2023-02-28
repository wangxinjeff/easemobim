package com.hyphenate.easemob.imlibs.mp.events;

public class EventLocUserAdded {
    private String username;
    private int chatType;
    private String to;

    public EventLocUserAdded(String to, String username, int chatType) {
        this.username = username;
        this.chatType = chatType;
        this.to = to;
    }

    public int getChatType() {
        return chatType;
    }

    public void setChatType(int chatType) {
        this.chatType = chatType;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
