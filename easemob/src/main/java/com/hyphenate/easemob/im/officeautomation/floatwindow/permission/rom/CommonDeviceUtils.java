package com.hyphenate.easemob.im.officeautomation.floatwindow.permission.rom;

import android.content.Context;

/**
 * Created by liyuzhao on 15/03/2018.
 */

public class CommonDeviceUtils extends BaseDeviceUtils {

	@Override
	public boolean checkFloatWindowPermission(Context context) {
		return true;
	}

	@Override
	public void applyPermission(Context context) {
		try {
			super.commonROMPermissionApplyInternal(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
