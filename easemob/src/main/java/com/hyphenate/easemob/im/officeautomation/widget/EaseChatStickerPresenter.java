package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowSticker;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 17/12/2018
 */
public abstract class EaseChatStickerPresenter extends EaseChatRowPresenter {

	@Override
	protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
		return new EaseChatRowSticker(cxt, message, position, adapter);
	}

	@Override
	public void onBubbleClick(EMMessage message) {
		super.onBubbleClick(message);

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
