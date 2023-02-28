package com.hyphenate.easemob.easeui.widget.gifpopupwindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.glide.GlideUtils;
import com.hyphenate.easemob.easeui.utils.EaseCommonUtils;
import com.hyphenate.easemob.easeui.widget.gifpopupwindow.bean.ExpressionItemBean;
import com.hyphenate.easemob.easeui.widget.gifpopupwindow.is.Func;
import com.hyphenate.easemob.easeui.widget.gifpopupwindow.is.SuspendListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 12/12/2018
 */
public class GifPopupWindow implements View.OnTouchListener, View.OnHoverListener {

	private Context context;
	// 悬浮监听器  暂时没用到
	private SuspendListener listener;
	// 用来显示动图视图的窗口
	private PopupWindow popupWindow;
	// 用来显示动图的控件
	private ImageView draweeView = null;
	// 窗口消失监听
	private PopupWindow.OnDismissListener onDismissListener;

	//这个是临时view
	private FrameLayout layout;
	//这个是acativity的根view
	private ViewGroup viewTop;
	//这个是临时view的属性
	FrameLayout.LayoutParams params;
	//数据 线程安全的Arraylist
	private List<ExpressionItemBean> itemBeans = Collections.synchronizedList(new ArrayList<ExpressionItemBean>());
	private HashSet<View> mViews = new HashSet<>();
	//这个hover到的view 的坐标
	private Rect activatedRect = null;
	//这个是hover到的view 也就是当前激活的view
	private View activatedView = null;

	public void setSuspendListener(SuspendListener listener){
		this.listener = listener;
	}

	public GifPopupWindow(){
	}

	// 获取该类实例
	public  GifPopupWindow getBuilder(Context context){
		this.context = context;
		onDismissListener = new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				mIsDisplay = false;
				removeCallBack();
			}
		};
		itemBeans.clear();
		layout = new FrameLayout(context);
		viewTop = (ViewGroup) ((Activity)context).getWindow().getDecorView().getRootView();
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		layout.setBackgroundColor(Color.TRANSPARENT);
		return this;
	}

	/**
	 * 该类的入口方法
	 * @param view 点击的View
	 * @return
	 */
	private GifPopupWindow setSuspendView(View view){
		setSuspendView(view, new Func<View>(){
			@Override
			public View invoke(){
				return null;
			}
		});
		return this;
	}

	/**
	 * 真正的入口
	 * @param view 点击的View
	 * @param copyView 可能会用到的缓存View
	 */
	private void setSuspendView(View view, final Func<View> copyView){
		if (view != null){
			view.setOnTouchListener(this);
		}
	}

	private void showWindow(View v, String remoteUrl){
		if (popupWindow == null) {
			LinearLayout view = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ease_gif_view, null);
			draweeView = view.findViewById(R.id.draweeview);
			int pxValue = EaseCommonUtils.convertDip2Px(context, 120);
			popupWindow = new PopupWindow(view, pxValue, pxValue);
			popupWindow.setFocusable(true);
			popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		GlideUtils.load(context, remoteUrl, R.drawable.ease_default_image, draweeView);
		int pos[] = new int[2];
		v.getLocationOnScreen(pos);
		int x = (v.getWidth() - popupWindow.getWidth()) / 2;
		popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, pos[0] + x, pos[1] - popupWindow.getHeight());
		mIsDisplay = true;
		mIsDraged = true;
		Rect rect = new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight());
		activatedRect = rect;
		Log.e("popupwindow is show", "" + mIsDraged);
	}

	/**
	 * 窗口消失
	 */
	private void dissDialog(){
		if (popupWindow == null)
			return;
		if (onDismissListener == null) {
			onDismissListener = new PopupWindow.OnDismissListener() {
				@Override
				public void onDismiss() {
					mIsDisplay = false;
					removeCallBack();
				}
			};
		}
		popupWindow.setOnDismissListener(onDismissListener);
		popupWindow.dismiss();
		Log.e("popupwindow is dismiss", "true");
	}


	private boolean performLongClick(){
		return true;
	}

	/**
	 * 模拟longclick 延时操作
	 * @param v
	 */
	private void postDelayed(final View v, final String remoteUrl){
		mHasPerformedLongPress = false;
		if (handler == null){
			handler = new Handler();
		}
		if (runnable == null){
			runnable = new Runnable(){
				@Override
				public void run() {
					if (performLongClick()){
						mHasPerformedLongPress = true;
					}
					if (!TextUtils.isEmpty(remoteUrl)){
						showWindow(v, remoteUrl);
					}
				}
			};
		}
		handler.postDelayed(runnable, Delay_Millis);
	}

	private void init(){
		viewTop.addView(layout, params);
	}

	private void removeView(){}

	public void initAndExecute(final View view, final String remoteUrl){
		view.post(new Runnable() {
			@Override
			public void run() {
				if (view != null && !mViews.contains(view)) {
					int location[] = new int[2];
					int width = view.getWidth();
					int height = view.getHeight();
					view.getLocationOnScreen(location);
					int left = location[0];
					int top = location[1];
					int right = left + width;
					int bottom = top + height;
					Rect rect = new Rect(left, top, right, bottom);
					ExpressionItemBean itemBean = new ExpressionItemBean();
					itemBean.setRect(rect);
					itemBean.setView(view);
					itemBean.setUri(remoteUrl);
					itemBeans.add(itemBean);
					//用来判断是否已经添加
					mViews.add(view);
					Log.e("itemBeans----->size", itemBean.toString() + "/" + itemBeans.size());
					Log.e("mViews----->size", mViews.toString() + "/" + mViews.size());
				}
			}
		});
		setSuspendView(view);
	}


	/**
	 * 移除所有延时操作
	 */
	private void removeCallBack(){
		if (handler != null && runnable != null){
			handler.removeCallbacks(runnable);
		}
		handler = null;
		runnable = null;
	}

	private Handler handler;
	private Runnable runnable;
	private boolean mIsDisplay = false;
	private boolean mIsDraged = false;
	private boolean mHasPerformedLongPress = false;
	private final static int Delay_Millis = 500;
	private final static int D_value = 150;
	private final static int INNER_VALUE = 5;// this is not neccessary

	float x = 0;
	float y = 0;

	public String getItemByView(View view){
		Iterator<ExpressionItemBean> iterator = itemBeans.iterator();
		while (iterator.hasNext()) {
			ExpressionItemBean bean = iterator.next();
			if (bean.getView() == view){
				return bean.getUri();
			}
		}
		return null;
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int Bounds[] = new int[2];
		v.getLocationOnScreen(Bounds);
		float width = v.getWidth();
		float height = v.getHeight();
		float left = Bounds[0] + INNER_VALUE;
		float top = Bounds[1] + INNER_VALUE;
		float right = left + width - INNER_VALUE;
		float bottom = top + height - INNER_VALUE;
		Rect rect = new Rect((int) left, (int) top, (int) right, (int) bottom);
		switch (event.getAction()) {
			case MotionEvent.ACTION_HOVER_MOVE:
			case MotionEvent.ACTION_DOWN:
				mHasPerformedLongPress = false;
				init();
				activatedView = v;
				x = event.getRawX();
				y = event.getRawY();
				if (mIsDisplay)
					return false;
				else {
					postDelayed(v, getItemByView(v));
				}
				Log.e("View onTouchEvent", "action down/return false");
				activatedView.setPressed(true);
				//focus!we must return false here,or parent view will not get the event,
				//if than ,scrolling container such as listview will not perform scroll when
				//you want it so
				return false;
			case MotionEvent.ACTION_MOVE:
				Log.e("View onTouchEvent", "action move/return true almost");
				if (!mIsDisplay) {
					//这里分两种情况  一种是没有触发显示事件的 一种是已经触发了的
					if (mIsDraged) {
						//已经触发了 而且触摸事件还没有结束
						//则在移动过程中 hover到其他表情view时 显示对应的dialog
						Iterator<ExpressionItemBean> iterator = itemBeans.iterator();
						while (iterator.hasNext()) {
							Log.e("mIsDragged is true", event.getRawX() + "/" + event.getRawY());
							ExpressionItemBean bean = iterator.next();
							Rect rect1 = bean.getRect();
							if (rect1.contains((int) event.getRawX(), (int) event.getRawY())) {
								activatedRect = rect1;
								activatedView.setPressed(false);
								activatedView = bean.getView();
								activatedView.setPressed(true);

								showWindow(bean.getView(), bean.getUri());
								return true;
							}
						}

					} else {
						//还没有触发显示事件(mIsDragged 为false)
						//而且还没显示出来(mIsDisplay为false) 手指的位移距离太大
						//或者移出了view的范围 取消延时操作
						//并且将触摸事件交给父级view去操作
						if (Math.abs(x - event.getRawX()) > D_value || Math.abs(y - event.getRawY()) > D_value || !rect.contains((int) event.getRawX(), (int) event.getRawY())) {
							removeCallBack();
							return false;
						}
					}
				} else {
					//理论上说 如果mIsDisplay为true 那么 mIsDragged也为true
					//但是mIsDisplay为false mIsDragged不一定为false 自己看代码
					if (mIsDraged) {
						if (!activatedRect.contains((int) event.getRawX(), (int) event.getRawY())) {
							dissDialog();
							activatedView.setPressed(false);
							Log.e("MotionEvent.Action_MOVE", "PopupWindow Dismiss");
						}
					}
					return true;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				mHasPerformedLongPress = false;
			case MotionEvent.ACTION_UP:
				//触摸事件取消了
				mIsDraged = false;
				removeCallBack();
				viewTop.removeView(layout);
				if (mIsDisplay) {
					dissDialog();
				}
				activatedView.setPressed(false);
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (!mHasPerformedLongPress) {
						// Only perform take click actions if we were in the pressed state
						if (activatedView.isPressed()) {
							// Use a Runnable and post this rather than calling
							// performClick directly. This lets other visual state
							// of the view update before click actions start.
							new Handler().post(new Runnable() {
								@Override
								public void run() {
									activatedView.performClick();
								}
							});
						}
					}
					Log.e("View onTouchEvent", "action up/return false");
				} else
					Log.e("View onTouchEvent", "action cancel/return false");
				//we are supposed to return false here,or something unpredicted will happen,
				//take the long click for a example,it will perform longclick no matter you have pressed
				//it for enough time or not
				return false;
			default:
				break;

		}
		return true;
	}


	@Override
	public boolean onHover(View v, MotionEvent event) {
		return false;
	}
}
