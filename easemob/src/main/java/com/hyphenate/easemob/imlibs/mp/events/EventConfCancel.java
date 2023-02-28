package com.hyphenate.easemob.imlibs.mp.events;

import com.hyphenate.chat.EMMessage;

public class EventConfCancel {

    private EMMessage mMessage;

    public EventConfCancel(EMMessage message) {
        this.mMessage = message;
    }

    public EMMessage getMessage() {
        return mMessage;
    }
}
