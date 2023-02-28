package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowRecall;

/**
 * Created by zhangsong on 17-10-12.
 */

public abstract class EaseChatRecallPresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowRecall(cxt, message, position, adapter);
    }
}
