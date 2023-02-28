package com.hyphenate.easemob.im.mp.listener;

import android.view.View;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 03/11/2018
 */
public abstract class NoDoubleClickListener implements View.OnClickListener {
	private final static int MIN_CLICK_TIME = 1000;
	private long lastClickTime = 0;

	public abstract void onNoDoubleClick(View view);

	@Override
	public void onClick(View v) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastClickTime > MIN_CLICK_TIME){
			lastClickTime = currentTime;
			onNoDoubleClick(v);
		}
	}
}
