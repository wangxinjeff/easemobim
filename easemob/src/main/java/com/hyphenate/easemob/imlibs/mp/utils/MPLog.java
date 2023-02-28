package com.hyphenate.easemob.imlibs.mp.utils;

import android.util.Log;

import com.hyphenate.util.EMLog;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 * 日志打印
 */
public class MPLog {

	private static boolean isDebug = true;

	public static void i(String tag, String content) {
		if (isDebug) {
			Log.i(tag, content);
			EMLog.i(tag, content);
		}
	}

	public static void d(String tag, String content) {
		if (isDebug) {
			Log.e(tag, content);
			EMLog.e(tag, content);
		}
	}

	public static void e(String tag, String content) {
		Log.e(tag, content);
		EMLog.e(tag, content);
	}

	public static void e(String tag, Exception e){
		if (e == null) return;
		e(tag, getStackTraceString(e));
	}

	public static String getStackTraceString(Throwable tr) {
		return Log.getStackTraceString(tr);
	}

}