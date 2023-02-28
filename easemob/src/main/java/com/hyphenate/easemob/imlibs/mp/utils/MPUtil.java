package com.hyphenate.easemob.imlibs.mp.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;
import com.hyphenate.easemob.imlibs.mp.MPClient;

import org.json.JSONObject;

import java.lang.reflect.Method;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 19/12/2018
 */
public final class MPUtil {

	private MPUtil() {
		throw new UnsupportedOperationException("unsupport init");
	}

	public static int getScreenWidth(){
		Context appContext = MPClient.get().getAppContext();
		if (appContext == null) {
			throw new IllegalStateException("plz first initialization");
		}
		WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	public static int getScreenHeight(){
		Context appContext = MPClient.get().getAppContext();
		if (appContext == null) {
			throw new IllegalStateException("plz first initialization");
		}
		WindowManager windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	public static JSONObject getDeviceJson() {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("devId", DeviceUtils.getUniqueDeviceId());
			jsonObj.put("devType", "Android");
			jsonObj.put("devName", getDeviceName());
			jsonObj.put("devModel", DeviceUtils.getModel());
			jsonObj.put("appVersion", AppUtils.getAppVersionName());
			jsonObj.put("systemCategory", DeviceUtils.getManufacturer());
			jsonObj.put("systemVersion", DeviceUtils.getSDKVersionName());
		}catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObj;
	}


	private static String getDeviceName() {
		String deviceName = "";
		try {
			BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
			if (myDevice != null) {
				deviceName = myDevice.getName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (TextUtils.isEmpty(deviceName)) {
			try {
				Class<?> cls = Class.forName("android.os.SystemProperties");
				Object object = cls.newInstance();
				Method getName = cls.getDeclaredMethod("get", String.class);
				deviceName = (String) getName.invoke(object, "persist.sys.device_name");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (TextUtils.isEmpty(deviceName)) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
				deviceName = Settings.Global.DEVICE_NAME;
			}
		}
		return deviceName;
	}


	/**
	 * 为每个HttpClient添加User-Agent
	 *
	 * @return
	 */
	public static String getDefaultUserAgent() {
		return "MP-Android(app:" + getAppVersion() +
				") " +
				getDeviceInfo();
	}

	private static int getAppVersion() {
		if (MPClient.get().getAppContext() == null) {
			return 0;
		}
		Context context = MPClient.get().getAppContext();
		PackageInfo pi = null;
		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return pi.versionCode;
		} catch (Exception ignored) {
		}
		return 0;
	}

	/**
	 * 获取手机设备信息（手机型号+手机厂商）
	 *
	 * @return
	 */
	private static String getDeviceInfo() {
		try {
			String model = Build.MODEL;
			String manufacturer = Build.MANUFACTURER;
			return "p:" + model + "," + manufacturer;
		} catch (Exception ignored) {
		}
		return "";
	}

	public static String getRealRequestUrl(String absoluteUrl) {
		if (absoluteUrl.startsWith("http")) {
			return absoluteUrl;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(MPClient.get().getAppServer());
		if (!absoluteUrl.startsWith("/")) {
			stringBuilder.append("/");
		}
		String encodeUrlParams = absoluteUrl;
		try {
			encodeUrlParams = urlEncodeURL(absoluteUrl);
		} catch (Exception e) {
		}
		stringBuilder.append(encodeUrlParams);
		return stringBuilder.toString();
	}

	private static String urlEncodeURL(String urlParams) {
		return urlParams;
//        try {
//            String result = URLEncoder.encode(urlParams, "UTF-8");
//            result = result.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%3F", "?").replaceAll("\\+", "%20");
//            return result;
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return null;
	}

}
