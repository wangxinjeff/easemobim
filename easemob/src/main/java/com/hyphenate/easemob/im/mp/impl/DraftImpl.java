package com.hyphenate.easemob.im.mp.impl;

import android.text.TextUtils;
import android.widget.EditText;

import com.hyphenate.easemob.easeui.delegates.DraftDelegate;
import com.hyphenate.easemob.easeui.domain.DraftEntity;
import com.hyphenate.easemob.easeui.ui.EaseChatFragment;
import com.hyphenate.easemob.easeui.utils.EaseSmileUtils;
import com.hyphenate.easemob.im.mp.manager.DraftManager;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 05/12/2018
 */
public class DraftImpl implements DraftDelegate {

	@Override
	public boolean hasDraft(String key) {
		return DraftManager.getInstance().hasDraft(key);
	}

	@Override
	public boolean removeDraft(String key) {
		 DraftManager.getInstance().removeDraft(key);
		 return true;
	}

	@Override
	public String getContent(String key) {
		DraftEntity draftEntity = DraftManager.getInstance().getDraftEntity(key);
		String content = draftEntity.getContent();
		if (!TextUtils.isEmpty(content)) {
			return content;
		}
		return "";
	}
}
