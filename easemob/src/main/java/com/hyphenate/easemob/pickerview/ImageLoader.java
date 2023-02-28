package com.hyphenate.easemob.pickerview;

import android.content.Context;
import android.widget.ImageView;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 */
public abstract class ImageLoader implements ImageLoaderInterface<ImageView>{

	@Override
	public ImageView createImageView(Context context) {
		return new ImageView(context);
	}
}
