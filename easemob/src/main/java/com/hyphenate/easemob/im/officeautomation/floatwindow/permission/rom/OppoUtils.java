package com.hyphenate.easemob.im.officeautomation.floatwindow.permission.rom;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Description:
 *
 * @author Shawn_Dut
 * @since 2018-02-01
 */
public class OppoUtils extends BaseDeviceUtils {

    private static final String TAG = "OppoUtils";

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
     * oppo ROM 权限申请
     */
    @Override
    public void applyPermission(Context context) {
        //merge request from https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        Intent intent = new Intent();
        //com.coloros.safecenter/.sysfloatwindow.FloatWindowListActivity
        ComponentName comp = new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");//悬浮窗管理页面
        intent.setComponent(comp);
        context.startActivity(intent);
        startSafely(context, intent);
    }

}
