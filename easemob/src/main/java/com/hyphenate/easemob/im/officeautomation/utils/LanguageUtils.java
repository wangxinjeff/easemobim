package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import androidx.annotation.RequiresApi;
import android.util.DisplayMetrics;

import com.hyphenate.easemob.imlibs.easeui.prefs.PreferenceUtils;

import java.util.Locale;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 28/09/2018
 */
public class LanguageUtils {

	public static Locale getSetLocale() {
		int currentLanguage = PreferenceUtils.getInstance().getMultiLanguage();
		switch (currentLanguage) {
			case ChangeLanguageHelper.LANGUAGE_DEFAULT:
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					return Resources.getSystem().getConfiguration().getLocales().get(0);
				} else {
					return Locale.getDefault();
				}
			case ChangeLanguageHelper.LANGUAGE_CHINA:
				return Locale.CHINA;
			case ChangeLanguageHelper.LANGUAGE_ENGLISH:
				return Locale.ENGLISH;
			default:
				return Locale.ENGLISH;
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	public static Context wrapContext(Context context) {
		Resources resources = context.getResources();
		Locale locale = LanguageUtils.getSetLocale();

		Configuration configuration = resources.getConfiguration();
		configuration.setLocale(locale);
		LocaleList localeList = new LocaleList(locale);
		LocaleList.setDefault(localeList);
		return context.createConfigurationContext(configuration);
	}

	public static void applyChange(Context context) {
		Resources res = context.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();

		Locale locale = getSetLocale();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			conf.setLocale(locale);
			LocaleList localeList = new LocaleList(locale);
			LocaleList.setDefault(localeList);
			conf.setLocales(localeList);
		} else {
			conf.locale = locale;
			try {
				conf.setLayoutDirection(locale);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		res.updateConfiguration(conf, dm);
	}

	public static Context attachBaseContext(Context context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			return wrapContext(context);
		}
		return context;
	}


}
