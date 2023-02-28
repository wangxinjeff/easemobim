package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.officeautomation.ui.ContactDetailsActivity;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowNameCard;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class EaseChatCardPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
        return new EaseChatRowNameCard(cxt, message, position, adapter);
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        super.onBubbleClick(message);
        JSONObject extJson;
        try {
            extJson = new JSONObject(message.getJSONObjectAttribute(Constant.EXT_EXTMSG).toString());
            JSONObject cardMsg = extJson.getJSONObject("content");

            Intent intent = new Intent(getContext(), ContactDetailsActivity.class);
            intent.putExtra("imUserId", cardMsg.getString("im_user_id"));
            getContext().startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        super.handleReceiveMessage(message);
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
