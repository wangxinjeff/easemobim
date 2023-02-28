package com.hyphenate.easemob.im.officeautomation.adapter;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.R;

import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 20/09/2018
 */
public class GroupsAdapter extends BaseQuickAdapter<MPGroupEntity, BaseViewHolder> {

	private Context mContext;
	public GroupsAdapter(Context ctx, int layoutResId, @Nullable List<MPGroupEntity> data) {
		super(layoutResId, data);
		this.mContext = ctx;
	}

	@Override
	protected void convert(BaseViewHolder helper, MPGroupEntity groupBean) {
		helper.setText(R.id.name, groupBean == null ? "" : groupBean.getName());
		if (groupBean != null && !TextUtils.isEmpty(groupBean.getAvatar())) {
			String remoteUrl = groupBean.getAvatar();
			GlideUtils.load(mContext, remoteUrl, R.drawable.ease_group_icon, helper.getView(R.id.avatar));
		} else {
			helper.setImageResource(R.id.avatar, R.drawable.ease_group_icon);
		}
	}
}
