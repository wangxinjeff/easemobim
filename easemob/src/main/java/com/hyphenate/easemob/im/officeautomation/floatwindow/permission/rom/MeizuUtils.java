/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.hyphenate.easemob.im.officeautomation.floatwindow.permission.rom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class MeizuUtils extends BaseDeviceUtils {
    private static final String TAG = "MeizuUtils";

    /**
     * 检测 meizu 悬浮窗权限
     */
    public boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    /**
     * 去魅族权限申请页面
     */
    public void applyPermission(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
//            intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");//remove this line code for fix flyme6.3
        intent.putExtra("packageName", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!startSafely(context, intent)){
            try {
                Log.e(TAG, "获取悬浮窗权限, 打开AppSecActivity失败, ");
                // 最新的魅族flyme 6.2.5 用上述方法获取权限失败, 不过又可以用下述方法获取权限了
                super.commonROMPermissionApplyInternal(context);
            } catch (Exception eFinal) {
                Log.e(TAG, "获取悬浮窗权限失败, 通用获取方法失败, " + Log.getStackTraceString(eFinal));
            }
        }
    }
}
