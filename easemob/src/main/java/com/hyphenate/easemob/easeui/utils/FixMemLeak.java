package com.hyphenate.easemob.easeui.utils;

import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 20/11/2018
 */
public class FixMemLeak {
	private Field field;
	private boolean hasField = true;

	public static FixMemLeak create(){
		return new FixMemLeak();
	}

	public void fixLeak(InputMethodManager imm){
		if (!hasField){
			return;
		}
//		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm == null) {
			return;
		}

		String[] arr = new String[]{"mLastSrvView"};
		for (String param : arr){
			try{
				if (field == null){
					field = imm.getClass().getDeclaredField(param);
				}
				if (field == null){
					hasField = false;
				}
				if (field != null){
					field.setAccessible(true);
					field.set(imm, null);
				}
			}catch (Throwable t){
			}
		}


	}

}
