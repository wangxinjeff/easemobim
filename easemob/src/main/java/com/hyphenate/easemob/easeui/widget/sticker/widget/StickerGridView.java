package com.hyphenate.easemob.easeui.widget.sticker.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import com.hyphenate.easemob.easeui.domain.EaseEmojicon;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class StickerGridView extends GridView {

	private GestureDetector gestureDetector;
	private boolean isInLongPress;
	private Rect rect = new Rect();
	private StickerGridItemView focusChild;
	private StickerPopupWindow popupWindow;
	private SingleTapClickListener singleTapClick;


	public void setSingleTapClick(SingleTapClickListener listener){
		this.singleTapClick = listener;
	}

	public StickerGridView(Context context) {
		super(context);
		init();
	}

	public StickerGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		this.gestureDetector = new GestureDetector(getContext(), new GestureTap());
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		gestureDetector.onTouchEvent(ev);
		switch (ev.getAction()){
			case MotionEvent.ACTION_MOVE:
				getParent().requestDisallowInterceptTouchEvent(isInLongPress);
				checkFocusChild(ev);
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				this.isInLongPress = false;
				if (this.focusChild != null){
					this.focusChild.setPressed(false);
				}
				this.focusChild = null;
				hidePopupWindow();
				break;
				default:break;
		}
		return true;
	}

	private class GestureTap extends GestureDetector.SimpleOnGestureListener
	{
		private GestureTap(){}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			for (int i = 0; i < getChildCount(); i++) {
				StickerGridItemView child = (StickerGridItemView) getChildAt(i);
				child.getHitRect(rect);
				if(rect.contains((int) e.getX(), (int) e.getY())){
					sendMessage(child.getEaseEmojicon());
					break;
				}

			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			isInLongPress = true;
			if (focusChild != null){
				showPopupWindow(focusChild);
			}
		}
	}

	private void checkFocusChild(MotionEvent ev){
		for (int i = 0; i < getChildCount(); i++) {
			StickerGridItemView child = (StickerGridItemView) getChildAt(i);
			child.getHitRect(rect);
			if (rect.contains((int)ev.getX(), (int)ev.getY())){
				if (this.focusChild == child){
					break;
				}
				onFocusChildChanged(focusChild, child);
				focusChild = child;
				break;
			}

		}
	}

	private void onFocusChildChanged(StickerGridItemView focusChild, StickerGridItemView newFocusChild)
	{
		if (focusChild != null){
			focusChild.setPressed(false);
		}
		if (newFocusChild != null){
			newFocusChild.setPressed(true);
		}
		if (isInLongPress){
			showPopupWindow(newFocusChild);
		}
	}

	private void sendMessage(EaseEmojicon easeEmojicon){
		if (singleTapClick != null){
			singleTapClick.onSingleTap(easeEmojicon);
		}
	}

	private int getChildIndex(View view){
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i) == view){
				return i;
			}
		}
		return -1;
	}

	private void showPopupWindow(StickerGridItemView view){
		hidePopupWindow();
		this.popupWindow = new StickerPopupWindow(getContext());
		int index = getChildIndex(view);
		int viewColumnIndex = getChildColumnIndex(index);
		StickerPopupWindow.Background bg;
		if (viewColumnIndex == 0) {
			bg = StickerPopupWindow.Background.LEFT;
		} else {
			if (viewColumnIndex < getNumColumns() - 1) {
				bg = StickerPopupWindow.Background.MIDDLE;
			} else {
				bg = StickerPopupWindow.Background.RIGHT;
			}

		}
		this.popupWindow.show(view, view.getEaseEmojicon(), bg);
	}

	private void hidePopupWindow(){
		if (this.popupWindow != null){
			this.popupWindow.dismiss();
		}
	}

	private int getChildColumnIndex(int index){
		return index % getNumColumns();
	}

	public static interface SingleTapClickListener{
		void onSingleTap(EaseEmojicon easeEmojicon);
	}


}
