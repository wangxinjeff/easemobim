package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.chat.EMMessage;

public class EventEMMessageReceived {
    private EMMessage message;

    public EventEMMessageReceived(EMMessage message) {
        this.message = message;
    }

    public EMMessage getMessage() {
        return message;
    }

    public void setMessage(EMMessage message) {
        this.message = message;
    }
}
