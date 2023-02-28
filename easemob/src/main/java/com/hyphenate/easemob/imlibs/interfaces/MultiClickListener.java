package com.hyphenate.easemob.imlibs.interfaces;

import android.view.View;

import com.hyphenate.easemob.imlibs.thirdpart.WeakHandler;

/**
 *
 * 单双击事件监听
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 15/01/2019
 */
public abstract class MultiClickListener implements View.OnClickListener {
	private static final int DelayedTime = 250;
	private boolean isDouble = false;
	private WeakHandler handler = new WeakHandler();

	private final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			isDouble = false;
			handler.removeCallbacks(this);
			onSingleClick();
		}
	};

	@Override
	public void onClick(View v) {
		if (isDouble){
			isDouble = false;
			handler.removeCallbacks(runnable);
			onDoubleClick();
		} else {
			isDouble = true;
			handler.postDelay(runnable, DelayedTime);
		}
	}

	public abstract void onSingleClick();

	public abstract void onDoubleClick();
}
