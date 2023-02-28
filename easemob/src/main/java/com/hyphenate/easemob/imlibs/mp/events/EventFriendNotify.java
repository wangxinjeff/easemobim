package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.chat.EMMessage;

public class EventFriendNotify {
    private EMMessage message;

    public EventFriendNotify(EMMessage message) {
        this.message = message;
    }

    public EMMessage getMessage() {
        return message;
    }

}
