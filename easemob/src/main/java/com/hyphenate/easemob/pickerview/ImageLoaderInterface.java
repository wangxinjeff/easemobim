package com.hyphenate.easemob.pickerview;

import android.content.Context;
import android.view.View;

import androidx.annotation.DrawableRes;

import java.io.Serializable;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 */
public interface ImageLoaderInterface<T extends View> extends Serializable {

	void displayImage(Context context, String path, T imageView);

	void displayImage(Context context, @DrawableRes Integer resId, T imageView);

	T createImageView(Context context);

}
