package com.hyphenate.easemob.im.mp.entity;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hyphenate.easemob.pickerview.ImageLoader;
import com.hyphenate.easemob.R;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 */
public class Loader extends ImageLoader {
	@Override
	public void displayImage(Context context, String path, ImageView imageView) {
		Glide.with(context).asBitmap().load(path).apply(RequestOptions.placeholderOf(R.drawable.fav_pic_default)).into(imageView);

	}

	@Override
	public void displayImage(Context context, Integer resId, ImageView imageView) {
		Glide.with(context).asBitmap().load(resId).apply(RequestOptions.placeholderOf(R.drawable.fav_pic_default)).into(imageView);
	}
}
