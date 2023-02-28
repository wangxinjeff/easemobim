package com.hyphenate.easemob.im.mp.adapter;

import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseUser;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.AvatarUtils;
import com.hyphenate.easemob.easeui.utils.EaseUserUtils;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.R;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 13/09/2018
 */
public class RecentAdapter extends BaseQuickAdapter<RecentItem, BaseViewHolder> {

	private boolean isShowMultiCheckBox;
	public RecentAdapter(int layoutResId, @Nullable List<RecentItem> data) {
		super(layoutResId, data);
	}

	public void setMultiCheckEnable(boolean enable){
		isShowMultiCheckBox = enable;
		notifyDataSetChanged();
	}

	private boolean isCurrentUser(String imUsername) {
		if(!TextUtils.isEmpty(imUsername)) {
			String realImUsername = imUsername;
			if (imUsername.contains("/")) {
				realImUsername = imUsername.split("/")[0];
			}
			if (realImUsername.equals(MPClient.get().getCurrentUser().getImUserId())) {
				return true;
			}
		}

		return false;
	}


	@Override
	protected void convert(BaseViewHolder helper,final RecentItem recentItem) {
		EMConversation conversation = recentItem.conversation;
		helper.setGone(R.id.cb, isShowMultiCheckBox);
		View cbView = ((CheckBox)helper.getView(R.id.cb));
		if (cbView != null){
			((CheckBox)cbView).setChecked(recentItem.isChecked);
		}
		if (conversation.getType() == EMConversation.EMConversationType.GroupChat) {
			String groupId = conversation.conversationId();
			GroupBean groupInfo = EaseUserUtils.getGroupInfo(groupId);
			if (groupInfo != null) {
				// group message, show group avatar
				String avatar = groupInfo.getAvatar();
				GlideUtils.load(EaseUI.getInstance().getContext(), avatar, R.drawable.ease_group_icon, helper.getView(R.id.iv_avatar));
				helper.setText(R.id.tv_content, groupInfo.getNick() != null ? groupInfo.getNick() : conversation.conversationId());
			} else {
				EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
				helper.setText(R.id.tv_content, group != null ? group.getGroupName() : conversation.conversationId());
				helper.setImageResource(R.id.iv_avatar, R.drawable.ease_group_icon);
			}
		}else {
			if ("admin".equals(conversation.conversationId())) {
				helper.setText(R.id.tv_content, R.string.system_msg);
				helper.setImageResource(R.id.iv_avatar, R.drawable.ease_default_avatar);
			} else if(isCurrentUser(conversation.conversationId())){
				helper.setText(R.id.tv_content, R.string.file_transfer);
				AvatarUtils.setAvatarContent(mContext, mContext.getString(R.string.file_transfer), helper.getView(R.id.iv_avatar));
			} else {
				EaseUser user = EaseUserUtils.getUserInfo(conversation.conversationId());
				AvatarUtils.setAvatarContent(mContext, user, helper.getView(R.id.iv_avatar));
//				if (user != null){
//					GlideUtils.load(AppHelper.getInstance().getAppContext(), user.getAvatar(), R.drawable.ease_default_avatar, helper.getView(R.id.iv_avatar));
//				}
				EaseUserUtils.setUserNick(conversation.conversationId(), helper.getView(R.id.tv_content));
			}
		}
	}
}
