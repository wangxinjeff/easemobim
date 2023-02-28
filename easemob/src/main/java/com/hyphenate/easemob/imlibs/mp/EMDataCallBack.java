package com.hyphenate.easemob.imlibs.mp;

public abstract class EMDataCallBack<T> {

	public abstract void onSuccess(T value);

	public abstract void onError(final int error, final String errorMsg);

	public void onAuthenticationException(){
	}

	public void onProgress(int progress){
	}
}
