package com.hyphenate.easemob.imlibs.mp;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 27/08/2018
 */

public interface ConnectionListener {
	void onConnected();
	void onAuthenticationFailed(int errorCode);
	void onDisconnected();
}
