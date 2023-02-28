package com.hyphenate.easemob.easeui.utils;

import android.text.TextUtils;

import com.hyphenate.chat.EMConversation;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 21/11/2018
 */
public class EaseConversationUtil {

	public static boolean isSticky(EMConversation conversation){
		if (conversation == null){
			return false;
		}
		return !TextUtils.isEmpty(conversation.getExtField());
	}


}
