package com.hyphenate.easemob.im.officeautomation.widget.chatrow;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easemob.im.mp.utils.UserProvider;
import com.hyphenate.easemob.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 23/10/2018
 */
public class EaseChatRowNotice extends EaseChatRow {

	private TextView contentView;
	private TextView tvClick;

	public EaseChatRowNotice(Context context, EMMessage message, int position, EaseMessageAdapter adapter) {
		super(context, message, position, adapter);
	}

	@Override
	protected void onInflateView() {
		inflater.inflate(R.layout.em_row_notice_message,this);
	}

	@Override
	protected void onFindViewById() {
		contentView = findViewById(R.id.text_content);
		tvClick = findViewById(R.id.tv_click);
	}

	@Override
	protected void onViewUpdate(EMMessage msg) {

	}

	@Override
	protected void onSetUpView() {
		try {
			JSONObject extMsgJson = message.getJSONObjectAttribute("extMsg");
			String type = extMsgJson.getString("type");
			String title = extMsgJson.optString("title");
			if(type.equals("vote_notice")){
				tvClick.setVisibility(VISIBLE);
				int userId = extMsgJson.optInt("userId");
				String content = extMsgJson.optString("content");
				int voteId = extMsgJson.optInt("voteId");
				if(userId != 0){
					if(userId == UserProvider.getInstance().getLoginUser().getUser_id()){
						title = "你" + title;
					} else {
						EaseUser easeUser = EaseUserUtils.getUserInfo(userId);
						title = (easeUser != null ? easeUser.getNickname() : "") + title;
					}
				}
				contentView.setText(title);
				tvClick.setText(content);
			} else {
				tvClick.setVisibility(GONE);
				JSONArray jsonArr = extMsgJson.optJSONArray("args");
				if (jsonArr != null && jsonArr.length() > 0) {
					Object[] argsArr = new String[jsonArr.length()];
					String contentNick = "";
					for (int i=0; i < jsonArr.length(); i++){
						String username = jsonArr.optString(i);
						if (EMClient.getInstance().getCurrentUser().equals(username)){
							contentNick = "我";
						}else{
							EaseUser easeUser = EaseUserUtils.getUserInfo(username);
							contentNick = (easeUser == null) ? "" : easeUser.getNick();
						}
						argsArr[i] = contentNick;
					}
					String content = String.format(title, argsArr);
					contentView.setText(content);
				} else {
					contentView.setText(title);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean disableCheckBox() {
		return true;
	}

	@Override
	protected boolean longClickEnable() {
		return false;
	}
}
