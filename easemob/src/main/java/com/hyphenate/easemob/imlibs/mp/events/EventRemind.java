package com.hyphenate.easemob.imlibs.mp.events;

public class EventRemind {

    private int type;
    private int id;

    public EventRemind(int type, int id) {
        this.type = type;
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }
}
