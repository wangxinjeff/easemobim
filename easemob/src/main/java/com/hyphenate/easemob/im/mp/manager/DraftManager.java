package com.hyphenate.easemob.im.mp.manager;

import android.text.TextUtils;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.domain.DraftEntity;
import com.hyphenate.easemob.im.mp.AppHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.WeakHashMap;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 05/12/2018
 */
public class DraftManager {

	private static DraftManager sManager;
	private static WeakHashMap<String, DraftEntity> draftCaches = new WeakHashMap<>();

	public static DraftManager getInstance(){
		if (sManager == null) {
			synchronized (DraftManager.class){
				if (sManager == null) {
					sManager = new DraftManager();
				}
			}
		}
		return sManager;
	}


	public void clear(){
		draftCaches.clear();
	}

	public boolean hasDraft(String id){
		return draftCaches.containsKey(id);
	}

	public void saveDraft(DraftEntity draftEntity){
		if (!TextUtils.isEmpty(draftEntity.getContent()) || !TextUtils.isEmpty(getReferenceMsgId(draftEntity))){
			draftCaches.put(draftEntity.getId(), draftEntity);
			AppHelper.getInstance().getModel().asyncSaveDraft(draftEntity);
		}else{
			draftCaches.remove(draftEntity.getId());
			AppHelper.getInstance().getModel().asyncRemoveDraft(draftEntity.getId());
		}
	}

	public void removeDraft(String id){
		if (draftCaches.containsKey(id)){
			draftCaches.remove(id);
		}
		AppHelper.getInstance().getModel().asyncRemoveDraft(id);
	}

	public DraftEntity getDraftEntity(String key){
		if (draftCaches.containsKey(key)) {
			return draftCaches.get(key);
		}
		return null;
	}

	public void loadDatas(){

		AppHelper.getInstance().getModel().asyncGetDrafts(new EMValueCallBack<List<DraftEntity>>() {
			@Override
			public void onSuccess(List<DraftEntity> draftEntities) {
				if (draftEntities != null && !draftEntities.isEmpty()){
					for (DraftEntity draftEntity : draftEntities){
						draftCaches.put(draftEntity.getId(), draftEntity);
					}
				}
			}

			@Override
			public void onError(int i, String s) {

			}
		});

	}

	/**
	 * 获取草稿保存的引用消息的msgId
	 * @param draftEntity
	 * @return
	 */
	public String getReferenceMsgId(DraftEntity draftEntity){
		if(draftEntity != null){
			String extra = draftEntity.getExtra();
			if(!TextUtils.isEmpty(extra)){
				try {
					JSONObject jsonObject = new JSONObject(extra);
					String msg = jsonObject.optString(EaseConstant.DRAFT_EXT_REFERENCE_MSG_ID);
					return msg;
				} catch (JSONException e) {
					e.printStackTrace();
					return "";
				}
			}
		}
		return "";
	}
}
