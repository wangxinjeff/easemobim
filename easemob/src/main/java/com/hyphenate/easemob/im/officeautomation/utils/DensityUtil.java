package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;

/**
 * Created by qby on 2017/9/19.
 * 尺寸工具类
 */

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue dp
     * @return 像素
     */
    public static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文
     * @param pxValue 像素
     * @return dp
     */
    public static int px2dp(Context context, int pxValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / density + 0.5f);
    }
}
