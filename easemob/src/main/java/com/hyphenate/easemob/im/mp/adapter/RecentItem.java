package com.hyphenate.easemob.im.mp.adapter;

import com.hyphenate.chat.EMConversation;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 13/09/2018
 */
public class RecentItem {
	public EMConversation conversation;
	public boolean isChecked;

	public RecentItem(EMConversation con){
		this.conversation = con;
	}

}
