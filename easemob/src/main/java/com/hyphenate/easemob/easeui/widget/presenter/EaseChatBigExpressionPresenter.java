package com.hyphenate.easemob.easeui.widget.presenter;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRowBigExpression;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by zhangsong on 17-10-12.
 */

public abstract class EaseChatBigExpressionPresenter extends EaseChatTextPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowBigExpression(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return;
        }

        // Send the group-ack cmd type msg if this msg is a ding-type msg.
        EaseDingMessageHelperV2.get().sendAckMessage(message);
    }
}
