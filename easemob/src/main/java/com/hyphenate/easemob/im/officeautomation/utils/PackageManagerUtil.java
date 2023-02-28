package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 09/10/2018
 */
public class PackageManagerUtil {

	private static List<String> mPackageNames = new ArrayList<>();
	private static final String GAODE_PACKAGE_NAME = "com.autonavi.minimap";
	private static final String BAIDU_PACKAGE_NAME = "com.baidu.BaiduMap";

	public static void initPackageManager(Context context) {
		PackageManager mPackageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = mPackageManager.getInstalledPackages(0);
		if (packageInfos != null && mPackageNames.isEmpty()) {
			for (int i = 0; i < packageInfos.size(); i++) {
				mPackageNames.add(packageInfos.get(i).packageName);
			}
		}
	}

	public static boolean haveGaodeMap(Context context) {
		initPackageManager(context);
		return mPackageNames.contains(GAODE_PACKAGE_NAME);
	}

	public static boolean haveBaiduMap(Context context) {
		initPackageManager(context);
		return mPackageNames.contains(BAIDU_PACKAGE_NAME);
	}

}
