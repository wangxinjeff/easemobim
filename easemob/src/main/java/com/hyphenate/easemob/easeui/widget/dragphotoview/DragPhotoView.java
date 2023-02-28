package com.hyphenate.easemob.easeui.widget.dragphotoview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.hyphenate.easemob.easeui.widget.photoview.EasePhotoView;
import com.hyphenate.easemob.easeui.widget.photoview.PhotoViewAttacher;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 17/09/2018
 *
 * 单指拖动PhotoView
 */
public class DragPhotoView extends EasePhotoView{
	private IDragView zoomParentView;

	public DragPhotoView(Context context) {
		this(context, null);
	}

	public DragPhotoView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public DragPhotoView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (null != zoomParentView && getScale() == 1) {
			boolean result = zoomParentView.dispatchEvent(event);
			if (result) {
				return true;
			}
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 设置跟随拖动进行缩放的布局
	 * @param zoomLayout 跟随拖动对应变化的布局
	 */
	public void setZoomParentView(IDragView zoomLayout){
		if (null == zoomParentView && null != zoomLayout) {
			zoomParentView = zoomLayout;
		}
	}

	/**
	 * 兼容使用PhotoView原生的设置单击监听
	 * @param listener
	 */
	@Override
	public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener) {
		super.setOnViewTapListener(listener);
	}
}
