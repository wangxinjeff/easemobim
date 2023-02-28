package com.hyphenate.easemob.easeui.widget.sticker.businesslogic;

import android.content.Context;
import android.os.Handler;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 18/12/2018
 */
public class StickerPackagesUiHandler {
	private static Handler uiHandler;

	public static void init(Context context)
	{
		uiHandler = new Handler(context.getMainLooper());
	}

	public static void destroy(){
		uiHandler = null;
	}

	public static Handler getUiHandler() {
		return uiHandler;
	}
}
