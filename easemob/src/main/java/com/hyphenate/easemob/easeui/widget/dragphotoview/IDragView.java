package com.hyphenate.easemob.easeui.widget.dragphotoview;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 17/09/2018
 *
 * 控件拖动接口
 *
 */
public interface IDragView {

	/**
	 * 拖动事件拦截
	 * @param event
	 * @return
	 */
	boolean dispatchEvent(MotionEvent event);

	/**
	 * 预绘制。在View的onDraw（canvas)之前调用。
	 * @param canvas
	 */
	void preOnDraw(Canvas canvas);

	/**
	 * 在View的onSizeChanged(int w, int h, int oldw, int oldh)之后调用
	 * @param w
	 * @param h
	 */
	void afterSizeChanged(int w, int h);

	/**
	 * 拖动结束判断
	 * @param event
	 */
	void dragFinishJudge(MotionEvent event);

}
