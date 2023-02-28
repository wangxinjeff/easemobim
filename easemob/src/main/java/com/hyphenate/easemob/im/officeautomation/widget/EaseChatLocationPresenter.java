package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMLocationMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.officeautomation.ui.EMBaiduMapActivity;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowLocation;

/**
 * Created by zhangsong on 17-10-12.
 */

public abstract class EaseChatLocationPresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowLocation(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMLocationMessageBody locBody = (EMLocationMessageBody) message.getBody();
        Intent intent = new Intent(getContext(), EMBaiduMapActivity.class);
        intent.putExtra("latitude", locBody.getLatitude());
        intent.putExtra("longitude", locBody.getLongitude());
        intent.putExtra("address", locBody.getAddress());
        getContext().startActivity(intent);
    }

}
