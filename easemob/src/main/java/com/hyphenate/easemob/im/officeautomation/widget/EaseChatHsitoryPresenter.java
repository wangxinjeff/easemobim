package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;
import android.content.Intent;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easemob.im.mp.ui.ChatsHistoryActivity;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowHistory;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 12/08/2018
 */

public abstract class EaseChatHsitoryPresenter extends EaseChatRowPresenter {
	@Override
	protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
		return new EaseChatRowHistory(cxt, message, position, adapter);
	}

	@Override
	public void onBubbleClick(EMMessage message) {
		super.onBubbleClick(message);
		if (getContext() == null){
			return;
		}
		if (message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
				&& message.getChatType() == EMMessage.ChatType.Chat) {
			try {
				EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			String extMsg = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG).toString();
			getContext().startActivity(new Intent(getContext(), ChatsHistoryActivity.class).putExtra("extMsg", extMsg));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
