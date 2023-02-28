/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.hyphenate.easemob.im.officeautomation.floatwindow.permission.rom;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RomUtils {
    private static final String TAG = "RomUtils";

    /**
     * 获取 emui 版本号
     * @return
     */
    public static double getEmuiVersion() {
        try {
            String emuiVersion = getSystemProperty("ro.build.version.emui");
            String version = emuiVersion.substring(emuiVersion.indexOf("_") + 1);
            return Double.parseDouble(version);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 4.0;
    }

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                Log.e(TAG, "get miui version code error, version : " + version);
            }
        }
        return -1;
    }

    public static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.e(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return line;
    }
    public static boolean checkIsHuaweiRom() {
        return Build.MANUFACTURER.contains("HUAWEI");
    }

    /**
     * check if is miui ROM
     */
    public static boolean checkIsMiuiRom() {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
    }

    public static boolean checkIsMeizuRom() {
        //return Build.MANUFACTURER.contains("Meizu");
        String meizuFlymeOSFlag  = getSystemProperty("ro.build.display.id");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)){
            return false;
        }else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")){
            return  true;
        }else {
            return false;
        }
    }

    public static boolean checkIs360Rom() {
        //fix issue https://github.com/zhaozepeng/FloatWindowPermission/issues/9
        return Build.MANUFACTURER.contains("QiKU")
                || Build.MANUFACTURER.contains("360");
    }

    public static boolean checkIsOppoRom() {
        //https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo");
    }

    public static boolean checkIsVivoRom(){
        String a = getSystemProperty("ro.vivo.os.name");
        return !TextUtils.isEmpty(a) && a.toLowerCase().contains("funtouch");
    }

    public static boolean checkIsSmartisanRom(){
        String a = getSystemProperty("ro.smartisan.version");
        return !TextUtils.isEmpty(a) && a.toLowerCase().contains("smartisan");
    }

    public static BaseDeviceUtils getDeviceUtils(){
        if (checkIsHuaweiRom()){
            return new HuaweiUtils();
        }else if (checkIsMiuiRom()){
            return new MiuiUtils();
        }else if (checkIsMeizuRom()){
            return new MeizuUtils();
        }else if (checkIsOppoRom()){
            return new OppoUtils();
        }else if (checkIs360Rom()){
            return new QikuUtils();
        }
        return new CommonDeviceUtils();
    }


    /**
     * 手机的权限 权限开启判断
     */
//    public static boolean surePermission(Context context){
//        final int version = Build.VERSION.SDK_INT;
//        if (checkIsVivoRom() || checkIsOppoRom()){
//            return false;
//        } else {
//            if (version >= Build.VERSION_CODES.M){
//                return checkOp(context, 24); // OP_SYSTEM_ALERT_WINDOW = 24;
//            } else {
//                return true;
//            }
//        }
//    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private static boolean checkOp(Context context, int op){
//        final int version = Build.VERSION.SDK_INT;
//        if (version >= 19) {
//            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//            try {
//                Class clazz = AppOpsManager.class;
//                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
//                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
//            } catch (Exception e) {
//                Log.e(TAG, Log.getStackTraceString(e));
//            }
//        } else {
//            Log.e(TAG, "Below API 19 cannot invoke!");
//        }
//        return false;
//    }

//    private static boolean startSafely(Context context, Intent intent){
//        if (context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//            return true;
//        } else {
//            Log.e(TAG, "Intent is not available! " + intent);
//            return false;
//        }
//    }



}
