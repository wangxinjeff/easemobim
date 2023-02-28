package com.hyphenate.easemob.pickerview;

import android.content.Context;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 */
public class SizeUtils {
	/**
	 * dp转px
	 *
	 * @param context 上下文
	 * @param dpValue dp值
	 * @return px值
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}


}
