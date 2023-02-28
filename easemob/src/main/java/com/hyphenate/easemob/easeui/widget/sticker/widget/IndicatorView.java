package com.hyphenate.easemob.easeui.widget.sticker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.hyphenate.easemob.R;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class IndicatorView extends LinearLayout{

	public IndicatorView(Context context){
		super(context);
	}

	public IndicatorView(Context context, AttributeSet attrs){
		super(context, attrs);
	}

	public void setCount(int count){
		updateView(count);
		setSelect(0);
	}

	public void setSelect(int index){
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			View view = getChildAt(i);
			if (i == index){
				view.setSelected(true);
			} else {
				view.setSelected(false);
			}
		}
	}

	private void updateView(int count){
		removeAllViews();
		for (int i = 0; i < count; i++) {
			View dotView = createDotView(i == 0);
			addView(dotView);
		}
	}

	private View createDotView(boolean isFirst){
		View view = new View(getContext());
		view.setBackgroundResource(R.drawable.indicator_dot);
		int size = getResources().getDimensionPixelSize(R.dimen.indicator_dot_size);
		LayoutParams lp = new LayoutParams(size, size);
		if (!isFirst){
			int marginLeft = getResources().getDimensionPixelOffset(R.dimen.indicator_dot_space);
			lp.setMargins(marginLeft, 0, 0, 0);
		}
		view.setLayoutParams(lp);
		return view;
	}


}
