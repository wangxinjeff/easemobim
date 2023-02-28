package com.hyphenate.easemob.im.officeautomation.widget;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.easeui.widget.presenter.EaseChatRowPresenter;
import com.hyphenate.easemob.im.officeautomation.widget.chatrow.EaseChatRowNotice;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */
public abstract class EaseChatNoticePresenter extends EaseChatRowPresenter {
	@Override
	protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, EaseMessageAdapter adapter) {
		return new EaseChatRowNotice(cxt, message, position, adapter);
	}
}
