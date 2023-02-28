package com.hyphenate.easemob.imlibs.mp.events;

public class EventTabReceived {
    int position;

    public EventTabReceived(int pos) {
        this.position = pos;
    }

    public int getPosition() {
        return position;
    }
}
