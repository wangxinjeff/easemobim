package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;
import com.hyphenate.easemob.R;

import java.util.Locale;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 28/09/2018
 */
public class ChangeLanguageHelper {

	public static final int LANGUAGE_CHINA = 1;
	public static final int LANGUAGE_ENGLISH = 2;
	public static final int LANGUAGE_DEFAULT = 0;

	private static String country = null;
	private static String mLanguage = "";
	private static Resources mResources;
	private static String mAutoCountry;

	public static void init(Context context){
		initResources(context);
		int currentLanguage = PreferenceUtils.getInstance().getMultiLanguage();
		switch (currentLanguage){
			case ChangeLanguageHelper.LANGUAGE_DEFAULT:
				country = context.getResources().getConfiguration().locale.getCountry();
				if ("TW".equals(country) || "HK".equals(country) || "MO".equals(country)) {
					country = "CN";
				}
				if ("CN".equals(country)) {
					mLanguage = "zh-CN";
				}else if ("US".equals(country)) {
					mLanguage = "en";
				}
				break;
			case ChangeLanguageHelper.LANGUAGE_CHINA:
				country = "CN";
				mLanguage = "zh-CN";
				break;
			case ChangeLanguageHelper.LANGUAGE_ENGLISH:
				country = "US";
				mLanguage = "en";
				break;
			default:
				country = context.getResources().getConfiguration().locale.getCountry();
				if ("CN".equals(country)) {
					mLanguage = "zh-CN";
				} else if ("US".equals(country)) {
					mLanguage = "en";
				}
				break;

		}
		mAutoCountry = Locale.getDefault().getCountry();
	}

	/**
	 * 获取当前字符串资源的内容
	 * @param id
	 * @return
	 */
	public static String getStringById(int id) {
		String string;
		if (mLanguage != null && !"".equals(mLanguage)) {
			string = mResources.getString(id, mLanguage);
		} else {
			string = mResources.getString(id, "");
		}

		if (string != null && string.length() > 0) {
			return string;
		}
		return "";
	}

	public static void changeLanguage(Context context, int language) {
		switch (language) {
			case LANGUAGE_CHINA:
				mLanguage = "zh-CN";
				country = "CN";
				PreferenceUtils.getInstance().setMultiLanguage(LANGUAGE_CHINA);
				break;
			case LANGUAGE_ENGLISH:
				mLanguage = "en";
				country = "US";
				PreferenceUtils.getInstance().setMultiLanguage(LANGUAGE_ENGLISH);
				break;
			case LANGUAGE_DEFAULT:
				country = mAutoCountry;
				if ("TW".equals(country) || "HK".equals(country) || "MO".equals(country)) {
					country = "CN";
				}
				if ("CN".equals(country)) {
					mLanguage = "zh-CN";
				} else if ("US".equals(country)) {
					mLanguage = "en";
				}
				PreferenceUtils.getInstance().setMultiLanguage(LANGUAGE_DEFAULT);
				break;
		}

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
			LanguageUtils.applyChange(context);
		}
	}

	public static boolean getDefaultLanguage() {
		return ("CN".equals(country));
	}

	public static String getLanguageName(Context context){
		int languageType = PreferenceUtils.getInstance().getMultiLanguage();
		if (languageType == LANGUAGE_DEFAULT){
			return context.getString(R.string.general_multi_language_auto);
		}else if(languageType == LANGUAGE_CHINA){
			return context.getString(R.string.general_multi_language_chinese);
		}else if (languageType == LANGUAGE_ENGLISH){
			return context.getString(R.string.general_multi_language_english);
		}
		return context.getString(R.string.general_multi_language_auto);
	}


	public static void initResources(Context context) {
		mResources = context.getResources();
	}
}
