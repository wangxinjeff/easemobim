package com.hyphenate.easemob.im.mp.manager;

import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easemob.easeui.EaseConstant;
import com.hyphenate.easemob.easeui.EaseUI;
import com.hyphenate.easemob.easeui.domain.StickerEntity;
import com.hyphenate.easemob.imlibs.mp.EMDataCallBack;
import com.hyphenate.easemob.imlibs.mp.utils.MPLog;
import com.hyphenate.easemob.imlibs.officeautomation.emrequest.EMAPIManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 14/12/2018
 */
public class StickerManager {

	private volatile static StickerManager sInstance;
	private static final String FILE_NAME = "stickers.txt";

	private List<StickerEntity> stickerEntities = Collections.synchronizedList(new ArrayList<>());
	private List<String> stickerMd5Vals = Collections.synchronizedList(new ArrayList<>());

	public static StickerManager get(){
		if (sInstance == null) {
			synchronized (StickerManager.class){
				if (sInstance == null){
					sInstance = new StickerManager();
				}
			}
		}
		return sInstance;
	}

	public void addBefore(final StickerEntity stickerEntity){
		stickerEntities.add(0, stickerEntity);
		if (stickerEntity.getMd5Val() != null){
			stickerMd5Vals.add(stickerEntity.getMd5Val());
		}
		EaseUI.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				saveDatas(convertStringFromDatas());
			}
		});
	}

	public void remove(int id){
		Iterator it = stickerEntities.iterator();
		while (it.hasNext()){
			StickerEntity item = (StickerEntity) it.next();
			if (item.getId() == id ){
				String md5Val = item.getMd5Val();
				if (stickerMd5Vals.contains(md5Val)){
					stickerMd5Vals.remove(md5Val);
				}
				it.remove();
				break;
			}
		}
		saveDatas(convertStringFromDatas());
	}

	public void syncFromServer(){
		EMAPIManager.getInstance().getStickers("", new EMDataCallBack<String>() {
			@Override
			public void onSuccess(String value) {
				MPLog.d("stickerManager", "getStickers:" + value);
				final List<StickerEntity> beanList = StickerManager.get().getEntitiesFromResult(value);
				if (beanList != null && !beanList.isEmpty()) {
					sortList(beanList);
					setNewDatas(beanList);
				}
			}

			@Override
			public void onError(int error, String errorMsg) {
			}
		});
	}

	private void sortList(List<StickerEntity> beanList){
		Collections.sort(beanList, new Comparator<StickerEntity>() {
			@Override
			public int compare(StickerEntity o1, StickerEntity o2) {
				return Integer.compare(o2.getRank(), o1.getRank());
			}
		});
	}

	public void loadDatas(){
		String result = readData();
		if (TextUtils.isEmpty(result)){

			return;
		}
		try {
			JSONArray jsonArray = new JSONArray(result);
			stickerEntities.clear();
			stickerEntities.addAll(getEntitiesFromJsonArr(jsonArray));
			stickerMd5Vals.clear();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public boolean containSticker(EMMessage message){
		try {
			String md5Val = message.getJSONObjectAttribute(EaseConstant.EXT_EXTMSG).getJSONObject(EaseConstant.EXT_MSGCONTENT).optString("md5");
			return containSticker(md5Val);
		}catch (Exception ignored){}
		return false;
	}

	public boolean containSticker(String md5Val){
		if (!TextUtils.isEmpty(md5Val)){
			if (stickerMd5Vals.contains(md5Val)){
				return true;
			}
			if (stickerEntities.isEmpty()){
				loadDatas();
			}
			synchronized (stickerEntities){
				for (StickerEntity entty : stickerEntities){
					if (md5Val.equals(entty.getMd5Val())){
						stickerMd5Vals.add(md5Val);
						return true;
					}
				}
			}

		}
		return false;
	}


	public void setNewDatas(List<StickerEntity> newDatas){
		stickerEntities.clear();
		stickerEntities.addAll(newDatas);
		saveDatas(convertStringFromDatas());
	}

	private String readData() {
		FileInputStream fis = null;
		byte[] buffer = null;

		String filePath = EMClient.getInstance().getCurrentUser() + "_" + FILE_NAME;
		try {
			File file = new File(filePath);
			if (!file.exists()){
				return null;
			}

			fis = EaseUI.getInstance().getContext().openFileInput(filePath);
			int fileLen = fis.available();
			buffer = new byte[fileLen];
			fis.read(buffer);
			return new String(buffer, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(fis);
		}
		return null;
	}



	private void saveDatas(String content){
		String filePath = EMClient.getInstance().getCurrentUser() + "_" + FILE_NAME;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = EaseUI.getInstance().getContext().openFileOutput(filePath, Context.MODE_PRIVATE);
			fileOutputStream.write(content.getBytes());
			fileOutputStream.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			close(fileOutputStream);
		}

	}

	public List<StickerEntity> getDataList(){
		if (stickerEntities.isEmpty()){
			loadDatas();
		}
		return stickerEntities;
	}


	public void close(Closeable stream) {
		try {
			if (stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public List<StickerEntity> getEntitiesFromResult(String value){
		try {
			JSONObject jsonResult = new JSONObject(value);
			JSONArray jsonEntities = jsonResult.optJSONArray("entities");
			if (jsonEntities != null){
				return getEntitiesFromJsonArr(jsonEntities);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public StickerEntity getEntityFromResult(String value){
		try {
			JSONObject jsonObject = new JSONObject(value);
			JSONObject jsonEntity = jsonObject.optJSONObject("entity");
			if (jsonEntity != null){
				return getEntityFromJSON(jsonEntity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<StickerEntity> getEntitiesFromJsonArr(JSONArray jsonArray){
		List<StickerEntity> stickerEntities = new ArrayList<>();
		if (jsonArray != null){
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObj = jsonArray.optJSONObject(i);
				if (jsonObj != null){
					StickerEntity stickerEntity = getEntityFromJSON(jsonObj);
					if (stickerEntity != null){
						stickerEntities.add(stickerEntity);
					}
				}
			}
		}
		return stickerEntities;
	}

	public StickerEntity getEntityFromJSON(JSONObject jsonObject){
		if (jsonObject == null){
			return null;
		}
		StickerEntity stickerEntity = new StickerEntity();
		stickerEntity.setId(jsonObject.optInt("id"));
		stickerEntity.setUrl(jsonObject.optString("url"));
		stickerEntity.setThumbnail(jsonObject.optString("thumbnail"));
		stickerEntity.setCreateTime(jsonObject.optLong("createTime"));
		stickerEntity.setLastUpdateTime(jsonObject.optLong("lastUpdateTime"));
		stickerEntity.setTenantId(jsonObject.optInt("tenantId"));
		stickerEntity.setUserId(jsonObject.optInt("userId"));
		stickerEntity.setGroupName(jsonObject.optString("groupName"));
		stickerEntity.setType(jsonObject.optString("type"));
		stickerEntity.setDelete(jsonObject.optBoolean("isDelete"));
		stickerEntity.setRank(jsonObject.optInt("rank"));
		stickerEntity.setMd5Val(jsonObject.optString("md5"));
		stickerEntity.setWidth(jsonObject.optInt("width", 0));
		stickerEntity.setHeight(jsonObject.optInt("height", 0));
		return stickerEntity;
	}

	public String convertStringFromDatas(){
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < stickerEntities.size(); i++) {
			JSONObject jsonObject = getJsonFromEntity(stickerEntities.get(i));
			if (jsonObject != null) {
				jsonArray.put(jsonObject);
			}
		}
		return jsonArray.toString();
	}

	public JSONObject getJsonFromEntity(StickerEntity entity){
		if (entity == null) {
			return null;
		}
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", entity.getId());
			jsonObject.put("groupName", entity.getGroupName());
			jsonObject.put("lastUpdateTime", entity.getLastUpdateTime());
			jsonObject.put("rank", entity.getRank());
			jsonObject.put("tenantId", entity.getTenantId());
			jsonObject.put("userId", entity.getUserId());
			jsonObject.put("thumbnail", entity.getThumbnail());
			jsonObject.put("url", entity.getUrl());
			jsonObject.put("type", entity.getType());
			jsonObject.put("createTime", entity.getCreateTime());
			jsonObject.put("md5", entity.getMd5Val());
			jsonObject.put("width", entity.getWidth());
			jsonObject.put("height", entity.getHeight());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}


	public void clearAll(){
		stickerMd5Vals.clear();
		stickerEntities.clear();
	}
}
