/*
 * Copyright (C) 2016 Facishare Technology Co., Ltd. All Rights Reserved.
 */
package com.hyphenate.easemob.im.officeautomation.floatwindow.permission.rom;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class QikuUtils extends BaseDeviceUtils {
    private static final String TAG = "QikuUtils";

    /**
     * 检测 360 悬浮窗权限
     */
    @Override
    public boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }


    /**
     * 去360权限申请页面
     */
    @Override
    public void applyPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!startSafely(context, intent)) {
            intent.setClassName("com.qihoo360.mobilesafe", "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (!startSafely(context, intent)) {
                Log.e(TAG, "can't open permission page with particular name, please use " +
                        "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page");
            }
        }
    }

}
