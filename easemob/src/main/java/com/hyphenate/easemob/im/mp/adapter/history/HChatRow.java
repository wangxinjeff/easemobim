package com.hyphenate.easemob.im.mp.adapter.history;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 21/11/2018
 */
public class HChatRow implements MultiItemEntity {

	public static final int TEXT = 1;
	public static final int IMG = 2;
//	public static final
	private int itemType;

	public HChatRow(int itemType){
		this.itemType = itemType;
	}

	@Override
	public int getItemType() {
		return itemType;
	}
}
