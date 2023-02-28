package com.hyphenate.easemob.im.mp.rest;

import com.hyphenate.EMError;
import com.hyphenate.easemob.easeui.domain.GroupBean;
import com.hyphenate.easemob.easeui.domain.MPGroupEntity;
import com.hyphenate.eventbus.MPEventBus;
import com.hyphenate.easemob.im.mp.AppHelper;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.events.EventGroupsReady;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;
import com.hyphenate.easemob.im.officeautomation.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 26/08/2018
 */

public final class EMJoinedGroupsRequest {

	private final static int size = 200;
	private final List<MPGroupEntity> joinedGroups = Collections.synchronizedList(new ArrayList<>());
	private int page;
	private EMDataCallBack<List<MPGroupEntity>> mDataCallBack;
//	private static Call mCall;

	public EMJoinedGroupsRequest setListener(EMDataCallBack<List<MPGroupEntity>> callBack){
		this.mDataCallBack = callBack;
		return this;
	}

	public void request(){
//		if (mCall != null && !mCall.isCanceled()){
//			mCall.cancel();
//		}
		page = 0;
		joinedGroups.clear();
		getJoinedGroups();
	}

	private void getJoinedGroups(){
		EMAPIManager.getInstance().getCollectedGroups(page, size, new EMDataCallBack<String>() {
			@Override
			public void onSuccess(final String value) {
				try {
					JSONObject jsonObj = new JSONObject(value);
					JSONArray jsonArrEntities = jsonObj.optJSONArray("entities");
					List<MPGroupEntity> groupEntities = MPGroupEntity.create(jsonArrEntities);
					boolean isLast = jsonObj.optBoolean("last", true);
					boolean isFirst = jsonObj.optBoolean("first", false);
					if (isFirst) {
						joinedGroups.clear();
					}
					if (isLast || groupEntities.isEmpty()) {
						synchronized (joinedGroups){
							joinedGroups.addAll(groupEntities);
//							for (int i = 0; i < groupEntities.size(); i++) {
//								MPGroupEntity groupEntity = groupEntities.get(i);
//								if (groupEntity != null) {
//									GroupBean groupBean = new GroupBean(groupEntity.getId(), groupEntity.getImChatGroupId(),
//											groupEntity.getName(), groupEntity.getAvatar(),
//											groupEntity.getCreateTime(), groupEntity.getType());
//									joinedGroups.add(groupBean);
//								}
//							}
							AppHelper.getInstance().getModel().saveGroupEntities(groupEntities);
							if (!joinedGroups.isEmpty()){
								PreferenceManager.getInstance().setCacheGroupsTime(System.currentTimeMillis());
							}
						}
						MPEventBus.getDefault().post(new EventGroupsReady());
						if (mDataCallBack != null){
							mDataCallBack.onSuccess(joinedGroups);
						}
					} else {
						page++;
						getJoinedGroups();
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (mDataCallBack != null){
						mDataCallBack.onError(EMError.GENERAL_ERROR, "jsonexception");
					}
				}

			}

			@Override
			public void onError(int error, String errorMsg) {
				if (mDataCallBack != null){
					mDataCallBack.onError(error, errorMsg);
				}
			}
		});
	}

	public static void cancel(){
//		if (mCall != null && !mCall.isCanceled()){
//			mCall.cancel();
//		}
	}




}
