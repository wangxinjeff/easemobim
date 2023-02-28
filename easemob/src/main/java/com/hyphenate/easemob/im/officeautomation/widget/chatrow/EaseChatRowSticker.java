package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelper;
import com.hyphenate.easemob.easeui.model.EaseDingMessageHelperV2;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.im.officeautomation.utils.Constant;

import org.json.JSONObject;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 17/12/2018
 */
public class EaseChatRowSticker extends EaseChatRow {

	private ImageView imageView;
	private String remoteUrl;

	public EaseChatRowSticker(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.em_row_received_sticker : R.layout.em_row_sent_sticker, this);
	}

	@Override
	protected void onFindViewById() {
		imageView = findViewById(R.id.image);
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
		try {
			JSONObject jsonExtMsg = message.getJSONObjectAttribute(Constant.EXT_EXTMSG);
			if (jsonExtMsg != null){
				JSONObject jsonContent = jsonExtMsg.optJSONObject(Constant.EXT_MSGCONTENT);
				if (jsonContent != null){
					remoteUrl = jsonContent.optString("remote_url");
					GlideUtils.load(getContext(), remoteUrl, R.drawable.ease_default_expression, imageView);
				}
			}
		} catch (HyphenateException e) {
			e.printStackTrace();
		}
	}

	private void onMessageCreate() {
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
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

	private EaseDingMessageHelperV2.IAckUserUpdateListener userUpdateListener =
			new EaseDingMessageHelperV2.IAckUserUpdateListener() {
				@Override
				public void onUpdate(List<String> list) {
					onAckUserUpdate(list.size());
				}
			};

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
	private void onMessageError() {
		progressBar.setVisibility(View.GONE);
		statusView.setVisibility(View.VISIBLE);
	}

	private void onMessageInProgress() {
		progressBar.setVisibility(View.VISIBLE);
		statusView.setVisibility(View.GONE);
	}
}
