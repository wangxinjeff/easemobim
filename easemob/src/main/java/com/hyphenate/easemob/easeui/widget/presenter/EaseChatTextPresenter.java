package com.hyphenate.easemob.easeui.widget.presenter;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.ui.EaseDingAckUserListActivity;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRowText;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by zhangsong on 17-10-12.
 */

public abstract class EaseChatTextPresenter extends EaseChatRowPresenter {
    private static final String TAG = "EaseChatTextPresenter";

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowText(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);

        if (!EaseDingMessageHelperV2.get().isDingMessage(message)) {
            return;
        }
        if (message.direct() == EMMessage.Direct.RECEIVE){
            return;
        }

        // If this msg is a ding-type msg, click to show a list who has already read this message.
        Intent i = new Intent(getContext(), EaseDingAckUserListActivity.class);
        i.putExtra("msg", message);
        getContext().startActivity(i);
    }

    @Override
    public abstract void onChecked(EMMessage message, boolean b);

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
