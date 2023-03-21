package com.hyphenate.easemob.imlibs.easeui.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 01/06/2018
 */

public class PreferenceUtils {

	private static String PREFERENCE_NAME = "deviceinfo";
	private static PreferenceUtils instance = new PreferenceUtils();
	private static SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;
	private Context mContext;

	private static final String SHARED_KEY_MULTI_LANG = "multi_language";
	private static final String SHARED_KEY_SET_TEXT_SIZE = "set_text_size";
	private static final String SHARED_KEY_ENTER_SEND_MESSAGE = "enter_send_message";
	private static final String SHARED_KEY_SESSION = "session";
	private static final String SHARED_KEY_NEW_SESSION = "new_session";
	private static final String SHARED_KEY_EARPIECE_PLAY_VOICE = "earpiece_player_voice";
	private static final String SHARED_KEY_LATEST_GROUP_TIME = "latest_group_time";

	private static final String SHARED_KEY_CRYPTED_PWD = "shared_key_crypted_pwd";

	private static final String SHARED_KEY_RECALL_DURATION = "shared_key_recall_duration";
	private static final String SHARED_KEY_SHOW_READ = "shared_key_show_read";
	private static final String SHARED_KEY_VOICE_DURATION = "shared_key_voice_duration";
	private static final String SHARED_KEY_SEARCH_RECORD = "shared_key_search_record";


	private PreferenceUtils() {
	}

	public static PreferenceUtils getInstance() {
		return instance;
	}

	public void init(Context context) {
		if (context != null) {
			this.mContext = context;
		}
		mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}


	public int getMultiLanguage() {
		return mSharedPreferences.getInt(SHARED_KEY_MULTI_LANG, 0);
	}

	public void setMultiLanguage(int multiLanguage) {
		editor.putInt(SHARED_KEY_MULTI_LANG, multiLanguage);
		editor.commit();
	}

	public void setCryptedPwd(String password){
		editor.putString(SHARED_KEY_CRYPTED_PWD, password).commit();
	}

	public String getSharedKeyCryptedPwd(){
		return mSharedPreferences.getString(SHARED_KEY_CRYPTED_PWD, "");
	}

	public int getTextSizeProgress(){
		return mSharedPreferences.getInt(SHARED_KEY_SET_TEXT_SIZE, 2);
	}

	public void setTextSizeProgress(int progress){
		editor.putInt(SHARED_KEY_SET_TEXT_SIZE, progress);
		editor.commit();
	}


	public boolean isEnterSendMsg(){
		return mSharedPreferences.getBoolean(SHARED_KEY_ENTER_SEND_MESSAGE, false);
	}

	public void setEnterSendMsg(boolean enterSendMsg){
		editor.putBoolean(SHARED_KEY_ENTER_SEND_MESSAGE, enterSendMsg);
		editor.commit();
	}

	/**
	 * 群组收到消息是否允许响铃
	 * false为不允许响铃，即开启了免打扰
	 * uid:当前用户账号
	 * groupId:目标群组id
	 */
	public boolean isEnableMsgRing(String uid, String groupId) {
		return mSharedPreferences.getBoolean(uid + "_" + groupId, true);

	}

	/**
	 * 设置是否允许响铃，true允许
	 */
	public void setEnableMsgRing(String uid, String groupId, boolean enable) {
		editor.putBoolean(uid + "_" + groupId, enable).apply();
	}


	public void setSession(String session){
		editor.putString(SHARED_KEY_SESSION, session);
		editor.commit();
	}

	public String getSession(){
		return mSharedPreferences.getString(SHARED_KEY_SESSION, null);
	}

	public void setNewSession(String session){
		editor.putString(SHARED_KEY_NEW_SESSION, session);
		editor.commit();
	}

	public String getNewSession(){
		return mSharedPreferences.getString(SHARED_KEY_NEW_SESSION, null);
	}

	public void setEarpieceVoice(boolean earpieceOn){
		editor.putBoolean(SHARED_KEY_EARPIECE_PLAY_VOICE, earpieceOn).apply();
	}

	public boolean isEarpieceOn(){
		return mSharedPreferences.getBoolean(SHARED_KEY_EARPIECE_PLAY_VOICE, false);
	}

	public long getLatestGroupRefreshTime(){
		return mSharedPreferences.getLong(SHARED_KEY_LATEST_GROUP_TIME, 0l);
	}

	public void setLatestGroupRefreshTime(long time){
		editor.putLong(SHARED_KEY_LATEST_GROUP_TIME, time).apply();
	}

	public long getRecallDuration(){
		return mSharedPreferences.getLong(SHARED_KEY_RECALL_DURATION, 120L);
	}

	public void setRecallDuration(long time){
		editor.putLong(SHARED_KEY_RECALL_DURATION, time).apply();
	}

	public long getVoiceDuration(){
		return mSharedPreferences.getLong(SHARED_KEY_VOICE_DURATION, 60L);
	}

	public void setVoiceDuration(long time){
		editor.putLong(SHARED_KEY_VOICE_DURATION, time).apply();
	}

	public boolean getShowRead(){
		return mSharedPreferences.getBoolean(SHARED_KEY_SHOW_READ, true);
	}

	public void setShowRead(boolean showRead){
		editor.putBoolean(SHARED_KEY_SHOW_READ, showRead).apply();
	}

	/**
	 * 保存搜索记录
	 * @param data
	 */
	public void saveSearchRecord(List<String> data){
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < data.size(); i ++){
			buffer.append(data.get(i));
			if(i != data.size() - 1){
				buffer.append(",");
			}
		}
		editor.putString(SHARED_KEY_SEARCH_RECORD, buffer.toString()).apply();
	}

	/**
	 * 获取搜索记录
	 * @return
	 */
	public List<String> getSearchRecord(){
		String s = mSharedPreferences.getString(SHARED_KEY_SEARCH_RECORD, "");
		String[] items = s.split(",");
		if(items.length == 1){
			if(TextUtils.isEmpty(items[0].trim())){
				return new ArrayList<>();
			}
		}
		return new ArrayList<>(Arrays.asList(items));
	}

	public void clearSearchRecord(){
		editor.putString(SHARED_KEY_SEARCH_RECORD, "").apply();
	}
}
