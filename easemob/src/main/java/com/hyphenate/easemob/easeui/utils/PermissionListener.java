package com.hyphenate.easemob.easeui.utils;

import androidx.annotation.NonNull;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 30/10/2018
 */
public interface PermissionListener {
	/**
	 * 通过授权
	 * @param permission
	 */
	void permissionGranted(@NonNull String[] permission);

	/**
	 * 拒绝授权
	 * @param permission
	 */
	void permissionDenied(@NonNull String[] permission);
}
