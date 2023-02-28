package com.hyphenate.easemob.im.mp.manager;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.im.officeautomation.domain.NoDisturbEntity;

import java.util.List;
import java.util.WeakHashMap;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 05/12/2018
 */
public class NoDisturbManager {

	private static NoDisturbManager sManager;
	private static WeakHashMap<String, NoDisturbEntity> noDisturbCaches = new WeakHashMap<>();

	public static NoDisturbManager getInstance(){
		if (sManager == null) {
			synchronized (NoDisturbManager.class){
				if (sManager == null) {
					sManager = new NoDisturbManager();
				}
			}
		}
		return sManager;
	}


	public void clear(){
		noDisturbCaches.clear();
	}

	public boolean hasNoDisturb(String id){
		return noDisturbCaches.containsKey(id);
	}

	public void saveNoDisturb(NoDisturbEntity noDisturbEntity){
		noDisturbCaches.put(noDisturbEntity.getId(), noDisturbEntity);
		AppHelper.getInstance().getModel().asyncSaveNoDisturb(noDisturbEntity);
	}

	public void removeNoDisturb(String id){
		if (noDisturbCaches.containsKey(id)){
			noDisturbCaches.remove(id);
		}
		AppHelper.getInstance().getModel().asyncRemoveNoDistrub(id);
	}

	public NoDisturbEntity getNoDisturbEntity(String key){
		if (noDisturbCaches.containsKey(key)) {
			return noDisturbCaches.get(key);
		}
		return null;
	}

	public void loadDatas(){

		AppHelper.getInstance().getModel().asyncGetNoDisturbs(new EMValueCallBack<List<NoDisturbEntity>>() {
			@Override
			public void onSuccess(List<NoDisturbEntity> disturbEntities) {
				if (disturbEntities != null && !disturbEntities.isEmpty()){
					for (NoDisturbEntity noDisturbEntity : disturbEntities){
						noDisturbCaches.put(noDisturbEntity.getId(), noDisturbEntity);
					}
				}
			}

			@Override
			public void onError(int i, String s) {

			}
		});
	}
}
