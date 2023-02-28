package com.hyphenate.easemob.easeui.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.hyphenate.easemob.R;
import com.hyphenate.easemob.easeui.domain.ImageItem;
import com.hyphenate.easemob.easeui.glide.GlideUtils;

/**
 * 推荐图片提示框 刚拍完照后，显示你可能要发送的图片
 */

public class EaseRecommendPhotoPop extends PopupWindow {

	private ImageView iv;
	private Context mContext;
	private RecommendPhotoListener mListener;
	public static ImageItem lastImage;

	public EaseRecommendPhotoPop(Context context){
		this.mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.ease_layout_pop_latestimage, null, false);
		iv = view.findViewById(R.id.recommondphoto_img);
		setContentView(view);
		setWidth(dip2px(90));
		setHeight(dip2px(135));
		setFocusable(false);
		setBackgroundDrawable(new BitmapDrawable());
		// 设置点击其他地方，就消失(只是设置这个，没有效果)
		setOutsideTouchable(true);
	}

	public void setImgPath(final String path){
		iv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null){
					mListener.onPhotoClicked(path);
				}
			}
		});
//		Glide.with(mContext).load(path).apply(RequestOptions.centerCropTransform()).into(iv);
//		Glide.with(mContext).load(path).centerCrop().into(iv);
		GlideUtils.load(mContext, path, iv);
	}

	public static EaseRecommendPhotoPop recommendPhoto(Context context, View view, String path, RecommendPhotoListener listener){
		final EaseRecommendPhotoPop recommendPhotoPop = new EaseRecommendPhotoPop(context);
		recommendPhotoPop.mListener = listener;
		recommendPhotoPop.setImgPath(path);
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		recommendPhotoPop.showAtLocation(view, Gravity.NO_GRAVITY, (location[0] + view.getWidth() / 2) - recommendPhotoPop.dip2px(92) / 2, location[1] - recommendPhotoPop.dip2px(138));
		return recommendPhotoPop;
	}


	public int dip2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, mContext.getResources().getDisplayMetrics());
	}


	public interface RecommendPhotoListener{
		void onPhotoClicked(String path);
	}


}
