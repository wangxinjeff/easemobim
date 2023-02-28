package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowRealTimeLoc;

public abstract class EaseChatRTLocPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowRealTimeLoc(cxt, message, position, adapter);
    }

}
