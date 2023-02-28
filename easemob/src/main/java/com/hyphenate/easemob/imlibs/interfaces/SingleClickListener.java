package com.hyphenate.easemob.imlibs.interfaces;

import android.view.View;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 16/01/2019
 * 防止短时间内触发多次点击事件
 */
public abstract class SingleClickListener implements View.OnClickListener {
	private static final String TAG = "SingleClickListener";

	private int MIN_CLICK_DELAY_TIME = 500;
	private long lastClickTime = 0;

	public SingleClickListener(){}

	public SingleClickListener(int MIN_CLICK_DELAY_TIME){
		this.MIN_CLICK_DELAY_TIME = MIN_CLICK_DELAY_TIME;
	}

	@Override
	public void onClick(View v) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME){
			lastClickTime = currentTime;
			onSingleClick(v);
		}
	}

	public abstract void onSingleClick(View v);

}
