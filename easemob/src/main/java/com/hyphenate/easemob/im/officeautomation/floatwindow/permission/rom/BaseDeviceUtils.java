package com.hyphenate.easemob.im.officeautomation.floatwindow.permission.rom;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by liyuzhao on 15/03/2018.
 */

public abstract class BaseDeviceUtils {

	private static String TAG = "BaseUtils";

	protected boolean startSafely(Context context, Intent intent){
		if (context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		} else {
			Log.e(TAG, "Intent is not available! " + intent);
			return false;
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	protected boolean checkOp(Context context, int op) {
		final int version = Build.VERSION.SDK_INT;
		if (version >= 19) {
			AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
			try {
				Class clazz = AppOpsManager.class;
				Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
				return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
			} catch (Exception e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		} else {
			Log.e(TAG, "Below API 19 cannot invoke!");
		}
		return false;
	}


	public abstract boolean checkFloatWindowPermission(Context context);

	public abstract void applyPermission(Context context);

	public void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
		Class clazz = Settings.class;
		Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");

		Intent intent = new Intent(field.get(null).toString());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse("package:" + context.getPackageName()));
		context.startActivity(intent);
	}

}
