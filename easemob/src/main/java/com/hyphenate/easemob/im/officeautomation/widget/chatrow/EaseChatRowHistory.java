package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.R;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 12/08/2018
 */

public class EaseChatRowHistory extends EaseChatRow {
	private TextView contentView;

	public EaseChatRowHistory(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
				R.layout.em_row_received_history : R.layout.em_row_sent_history, this);
	}

	@Override
	protected void onFindViewById() {
		contentView = (TextView) findViewById(R.id.tv_chatcontent);
	}

	@Override
	protected void onViewUpdate(EMMessage msg) {
		switch (msg.status()) {
			case CREATE:
				onMessageCreate();
				break;
			case SUCCESS:
				onMessageSuccess();
				break;
			case FAIL:
				onMessageError();
				break;
			case INPROGRESS:
				onMessageInProgress();
				break;
		}
	}

	@Override
	protected void onSetUpView() {
		EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
		Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
		// 设置内容
		contentView.setText(span, TextView.BufferType.SPANNABLE);
		int currentProgress = PreferenceUtils.getInstance().getTextSizeProgress();
		int defaultTextSize = 15;
		float x = 0.1f*(currentProgress - 2 ) + 1;
		contentView.setTextSize(defaultTextSize * x);
	}

	@Override
	protected boolean disableCheckBox() {
		return true;
	}

	public void onAckUserUpdate(final int count) {
		if (ackedView != null) {
			ackedView.post(new Runnable() {
				@Override
				public void run() {
					ackedView.setVisibility(VISIBLE);
					ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
				}
			});
		}
	}

	private void onMessageCreate() {
		progressBar.setVisibility(View.VISIBLE);
		statusView.setVisibility(View.GONE);
	}

	private void onMessageSuccess() {
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.GONE);

		// Show "1 Read" if this msg is a ding-type msg.
		if (EaseDingMessageHelperV2.get().isDingMessage(message) && ackedView != null) {
			ackedView.setVisibility(VISIBLE);
			int count = message.groupAckCount();
			ackedView.setText(String.format(getContext().getString(R.string.group_ack_read_count), count));
		}

		// Set ack-user list change listener.
		EaseDingMessageHelperV2.get().setUserUpdateListener(message, userUpdateListener);
	}

	private void onMessageError() {
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
	}

	private void onMessageInProgress() {
		progressBar.setVisibility(View.VISIBLE);
		statusView.setVisibility(View.GONE);
	}

	private EaseDingMessageHelperV2.IAckUserUpdateListener userUpdateListener =
			new EaseDingMessageHelperV2.IAckUserUpdateListener() {
				@Override
				public void onUpdate(List<String> list) {
					onAckUserUpdate(list.size());
				}
			};
}
