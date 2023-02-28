package com.hyphenate.easemob.easeui.widget.sticker.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;
import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.EaseEmojicon;
import com.hyphenate.easemob.imlibs.mp.MPClient;
import com.hyphenate.easemob.imlibs.mp.utils.MPUtil;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class StickerPopupWindow extends PopupWindow {

	private static StickerPopupWindow instance;

	public static synchronized StickerPopupWindow getInstance(Context context){
		if (instance == null){
			instance = new StickerPopupWindow(context);
		}
		return instance;
	}

	StickerPopupWindow(Context context){
		View view = LayoutInflater.from(context).inflate(R.layout.sticker_popup, null);
		setContentView(view);
		setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
	}

	public void show(View view, EaseEmojicon easeEmojicon, Background bg){
		if (easeEmojicon.getType() == EaseEmojicon.Type.NORMAL){
			return;
		}
		View contentView = getContentView();
		View background = contentView.findViewById(R.id.bg);
		background.setBackgroundResource(bg.resId);
//		final GifView gifImageView = contentView.findViewById(R.id.gif_view);
		final ImageView gifImageView = contentView.findViewById(R.id.gif_view);
		if (easeEmojicon.getType() == EaseEmojicon.Type.BIG_EXPRESSION){
			Glide.with(MPClient.get().getAppContext()).load(easeEmojicon.getBigIcon()).into(gifImageView);
//			gifImageView.setGifResource(easeEmojicon.getBigIcon());
		} else if (easeEmojicon.getType() == EaseEmojicon.Type.STICKER){
			String remoteUrl = easeEmojicon.getRemoteUrl();
			if (remoteUrl == null){
				return;
			}
			if (!remoteUrl.startsWith("http")) {
				remoteUrl = EaseUI.getInstance().getAppServer() + remoteUrl;
			}
			Glide.with(EaseUI.getInstance().getContext()).load(remoteUrl).into(gifImageView);
//			GifImageLoader.getInstance().obtain(easeEmojicon, new GifImageLoader.SimpleCallback() {
//				@Override
//				public void onSuccess(byte[] bytes) {
//					gifImageView.setBytes(bytes);
//					gifImageView.play();
//				}
//
//				@Override
//				public void onFail() {
//					gifImageView.setBytes(null);
//				}
//			});
		}

		int yoff;
		int xoff;
		if (contentView.getHeight() == 0){
			int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(MPUtil.getScreenWidth(), View.MeasureSpec.UNSPECIFIED);
			int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(MPUtil.getScreenHeight(), View.MeasureSpec.UNSPECIFIED);
			contentView.measure(widthMeasureSpec, heightMeasureSpec);
			xoff = getXoff(view, bg, contentView.getMeasuredWidth());
			yoff = -(view.getHeight() + contentView.getMeasuredHeight());
		} else {
			xoff = getXoff(view, bg, contentView.getWidth());
			yoff = -(view.getHeight() + contentView.getHeight());
		}
		Log.e("popupWindow", "xoff:" + xoff + ",yoff:" + yoff);
		showAsDropDown(view, xoff, yoff);
	}

	private int getXoff(View view, Background bg, int bgWidth){
		switch (bg){
			case MIDDLE:
				return (view.getWidth() - bgWidth) / 2;
			case LEFT:
				return (view.getWidth() - bgWidth) / 2 + view.getContext().getResources().getDimensionPixelOffset(R.dimen.popup_window_xoff);
			case RIGHT:
				return (view.getWidth() - bgWidth) / 2 - view.getContext().getResources().getDimensionPixelOffset(R.dimen.popup_window_xoff);

		}
		return 0;
	}

	public static enum Background
	{
		LEFT(R.drawable.sticker_zuoyulan),
		MIDDLE(R.drawable.sticker_zhongyulan),
		RIGHT(R.drawable.sticker_youyulan);

		private final int resId;

		private Background(int resId){
			this.resId = resId;
		}


	}



}
