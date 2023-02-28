package com.hyphenate.easemob.im.mp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import java.lang.reflect.Method;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 29/10/2018
 */
public class NotificationUtil {

	public static void clearNotification(Context context, int notificationId){
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancel(notificationId);
	}

	public static int getRingMode(Context context){
		AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		return am.getRingerMode();
	}
	private static Notification createNotificationNew(Context appContext, String title, String content, PendingIntent pendingIntent, int defaults) {

		NotificationManager nm = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
		String tickerText = null;
		try {
			tickerText = appContext.getResources().getString(appContext.getResources().getIdentifier("mp_notification_ticker_text", "string",
					appContext.getPackageName()));
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
		if (tickerText == null) {
			tickerText = content;
		}

		PackageManager packageManager = appContext.getPackageManager();
		String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
		// notification title
		String contentTitle = TextUtils.isEmpty(title) ? appname : title;
		// create and send notification
		NotificationCompat.Builder mBuilder;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

			NotificationChannel chan = new NotificationChannel("default", "channel", NotificationManager.IMPORTANCE_DEFAULT);
			chan.setLightColor(Color.GREEN);
			chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
			chan.setVibrationPattern(new long[]{0, 1000, 500, 1000});
			chan.enableVibration(true);
			nm.createNotificationChannel(chan);
		}

		mBuilder = new NotificationCompat.Builder(appContext, "default")
				.setSmallIcon(appContext.getApplicationInfo().icon)
				.setAutoCancel(false);

		mBuilder.setContentTitle(contentTitle);
		mBuilder.setTicker(tickerText);
		mBuilder.setContentText(content);
		mBuilder.setDefaults(defaults);
		mBuilder.setContentIntent(pendingIntent);
		return mBuilder.build();
	}

	private static Notification createNotification(Context context, String title, String content, PendingIntent pendingIntent, int defaults) {
		String tickerText = null;
		try {
			tickerText = context.getResources().getString(context.getResources().getIdentifier("mp_notification_ticker_text", "string", context.getPackageName()));
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
		if (tickerText == null) {
			tickerText = content;
		}
		Notification notification = null;
		if (Build.VERSION.SDK_INT < 11) {
			try {
				notification = new Notification(context.getApplicationInfo().icon, tickerText, System.currentTimeMillis());

				Class<?> classType = Notification.class;
				Method method = classType.getMethod("setLatestEventInfo", new Class[]{Context.class, CharSequence.class, PendingIntent.class});
				method.invoke(notification, new Object[]{content, title, content, pendingIntent});
				notification.flags = 48;
				notification.defaults = -1;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			boolean isLollipop = Build.VERSION.SDK_INT >= 21;
			int smallIcon = context.getResources().getIdentifier("notification_small_icon", "drawable", context.getPackageName());
			if ((smallIcon <= 0) || (!isLollipop)) {
				smallIcon = context.getApplicationInfo().icon;
			}
			Bitmap appIcon = getAppIcon(context);
			Notification.Builder builder = new Notification.Builder(context);
			if(appIcon != null) {
				builder.setLargeIcon(appIcon);
			}
			builder.setSmallIcon(smallIcon);
			builder.setTicker(tickerText);
			builder.setContentTitle(title);
			builder.setContentText(content);
			builder.setContentIntent(pendingIntent);
			builder.setAutoCancel(true);
			builder.setOngoing(true);
			builder.setOngoing(true);
			builder.setDefaults(defaults);
			notification = builder.getNotification();
		}
		return notification;
	}

	/**
	 * 获取应用图标
	 * @param context
	 * @return
	 */
	public static synchronized Bitmap getAppIcon(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}

		Drawable d = packageManager.getApplicationIcon(applicationInfo);
		BitmapDrawable db = (BitmapDrawable) d;
		Bitmap bm = db.getBitmap();
		return bm;
	}

	public static void showNotification(Context context, String title, String content, PendingIntent pendingIntent, int notificationId, int defaults){
		Notification notification = createNotificationNew(context, title, content, pendingIntent, defaults);
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notification != null){
			nm.notify(notificationId, notification);
		}
	}

	public static void showNotification(Context context, String title, String content, PendingIntent pendingIntent, int notificationId){
		showNotification(context, title, content, pendingIntent, notificationId, -1);
	}
}
