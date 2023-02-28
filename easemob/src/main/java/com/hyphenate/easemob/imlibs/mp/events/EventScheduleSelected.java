package com.hyphenate.easemob.imlibs.mp.events;

public class EventScheduleSelected {
    long scheduleTime;
    public EventScheduleSelected(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }
}
