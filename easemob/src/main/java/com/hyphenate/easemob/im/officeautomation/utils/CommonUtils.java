package com.hyphenate.easemob.im.officeautomation.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.media.AudioManager;
import android.util.Log;

import com.hyphenate.easemob.imlibs.mp.utils.MPLog;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 21/08/2018
 */

public class CommonUtils {

	// 先定义一个常量，这个是耳机当前状态文件
	private static final String HEADSET_STATE_PATH = "/sys/class/switch/h2w/state";


	public static boolean isHeadsetOn(Context context){
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return audioManager.isWiredHeadsetOn();
	}

	/**
	 * 获取耳机当前是否为插入状态
	 * @return
	 */
	public static boolean getHeadsetState(){
		// 用读文件的方式读取状态码后做判断，很简单
		char[] buffer = new char[1024];
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(HEADSET_STATE_PATH);
			int len = fileReader.read(buffer, 0, 1024);
			int newState = Integer.valueOf(new String(buffer, 0, len).trim());
			MPLog.e("CommonUtil", "getHeadsetState:" + newState);
			//newState 0: 未插入耳机  1：插入耳机
			return newState == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (fileReader != null){
				try {
					fileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}




	/**
	 * 获取版本号
	 */
	public static String getAppVersionName(Context context){
		String versionName = "";
		int versionCode = 0;
		try {
			// --- get the package info ---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm .getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			versionCode = pi.versionCode;
			if (versionName == null || versionName.length() <= 0){
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}

		return versionName + "." + versionCode;

	}

	/**
	 * MD5加密
	 * @param input
	 * @return
	 */
	public static String getMd5Hash(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String md5 = number.toString(16);

			while (md5.length() < 32)
				md5 = "0" + md5;

			return md5;
		} catch (NoSuchAlgorithmException e) {
			Log.e("MD5", e.getLocalizedMessage());
			return null;
		}
	}

	/**
	 * 检查手机上是否安装了指定的软件
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isAvailable(Context context, String packageName) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		List<String> packageNames = new ArrayList<>();
		if (packageInfos != null) {
			for (int i = 0; i < packageInfos.size(); i++) {
				String packName = packageInfos.get(i).packageName;
				packageNames.add(packName);
			}
		}
		// 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
		return packageNames.contains(packageName);
	}

	/**
	 * Gets the height of the status bar
	 */
	public static int getStatusBarHeight(Context context) {
		Resources resources = context.getResources();
		int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
		return resources.getDimensionPixelSize(resourceId);
	}
}
