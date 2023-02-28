package com.hyphenate.easemob.imlibs.mp.events.frtc;

public class EventCallCancel {
    public String meetingNum;
    public String fromUserId;

    public EventCallCancel(String meetingNum, String fromUserId) {
        this.meetingNum = meetingNum;
        this.fromUserId = fromUserId;
    }

}
