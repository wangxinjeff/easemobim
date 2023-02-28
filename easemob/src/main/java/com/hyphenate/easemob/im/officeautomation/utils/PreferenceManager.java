/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easemob.im.officeautomation.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.hyphenate.easemob.imlibs.mp.prefs.PrefsUtil;

import org.json.JSONObject;

public class PreferenceManager {
    /**
     * name of preference
     */
    public static final String PREFERENCE_NAME = "oa_saveInfo";
    private static SharedPreferences mSharedPreferences;
    private static PreferenceManager mPreferencemManager;
    private static SharedPreferences.Editor editor;

    private String SHARED_KEY_SETTING_NOTIFICATION_SHOW_DETAILS = "shared_key_setting_notification_show_details";
    private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
    private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
    private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
    private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";

    private static String SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE = "shared_key_setting_chatroom_owner_leave";
    private static String SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP = "shared_key_setting_delete_messages_when_exit_group";
    private static String SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER = "shared_key_setting_transfer_file_by_user";
    private static String SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL = "shared_key_setting_autodownload_thumbnail";
    private static String SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION = "shared_key_setting_auto_accept_group_invitation";
    private static String SHARED_KEY_SETTING_OFFLINE_PUSH_CALL = "shared_key_setting_offline_push_call";

    private static String SHARED_KEY_SETTING_GROUPS_SYNCED = "SHARED_KEY_SETTING_GROUPS_SYNCED";
    private static String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";

    private static String SHARED_KEY_CALL_MIN_VIDEO_KBPS = "SHARED_KEY_CALL_MIN_VIDEO_KBPS";
    private static String SHARED_KEY_CALL_MAX_VIDEO_KBPS = "SHARED_KEY_CALL_Max_VIDEO_KBPS";
    private static String SHARED_KEY_CALL_MAX_FRAME_RATE = "SHARED_KEY_CALL_MAX_FRAME_RATE";
    private static String SHARED_KEY_CALL_AUDIO_SAMPLE_RATE = "SHARED_KEY_CALL_AUDIO_SAMPLE_RATE";
    private static String SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION = "SHARED_KEY_CALL_BACK_CAMERA_RESOLUTION";
    private static String SHARED_KEY_CALL_FRONT_CAMERA_RESOLUTION = "SHARED_KEY_FRONT_CAMERA_RESOLUTIOIN";
    private static String SHARED_KEY_CALL_FIX_SAMPLE_RATE = "SHARED_KEY_CALL_FIX_SAMPLE_RATE";

    private static final String SHARED_KEY_LOGIN_USER = "login_user";
    private static final String SHARED_KEY_LOGIN_APPKEY = "login_appkey";

    private static final String SHARED_KEY_CACHE_NAME_PRE_TIME = "cache_pre_time";
    private static final String SHARED_KEY_LAST_CACHE_USERS_TIME = "last_cache_users_time";
    private static final String SHARED_KEY_LAST_CACHE_ORG_USERS_TIME = "last_cache_org_users_time";
    private static final String SHARED_KEY_CACHE_GROUP_TIME = "cache_group_time";

    private static final String SHARED_KEY_LAST_CACHE_USERNAME = "last_cache_username";
    private static final String SHARED_KEY_LAST_CACHE_PASSWORD = "last_cache_password";
    private static final String SHARED_KEY_LAST_LOGIN_STATUS = "last_login_status";
    private static final String SHARED_KEY_SERVER_IP_CONFIG = "server_ip_config";

    @SuppressLint("CommitPrefEdits")
    private PreferenceManager(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mPreferencemManager == null) {
            mPreferencemManager = new PreferenceManager(cxt);
        }
    }

    /**
     * get instance of PreferenceManager
     *
     * @param
     * @return
     */
    public synchronized static PreferenceManager getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }
        return mPreferencemManager;
    }

    public boolean isAdmin() {
        String loginUser = PrefsUtil.getInstance().getLoginUser();
        try {
            JSONObject jsonObject = new JSONObject(loginUser);
            String roles = jsonObject.optString("type");
            if (roles != null && roles.contains("admin")) {
                return true;
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    public void setSettingMsgNotification(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
        editor.apply();
    }

    public boolean getSettingMsgNotification() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
    }

    public void setSettingMsgSound(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
        editor.apply();
    }

    public boolean getSettingMsgSound() {

        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
    }

    public void setSettingMsgVibrate(boolean paramBoolean) {
        editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
        editor.apply();
    }

    public boolean getSettingMsgVibrate() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
    }

    public boolean getSettingMsgSpeaker() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
    }

    public boolean getSettingAllowChatroomOwnerLeave() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_CHATROOM_OWNER_LEAVE, true);
    }

    public boolean isDeleteMessagesAsExitGroup() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_DELETE_MESSAGES_WHEN_EXIT_GROUP, true);
    }

    public boolean isSetTransferFileByUser() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_TRANSFER_FILE_BY_USER, true);
    }

    public boolean isSetAutodownloadThumbnail() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_AUTODOWNLOAD_THUMBNAIL, true);
    }

    public boolean isAutoAcceptGroupInvitation() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_AUTO_ACCEPT_GROUP_INVITATION, true);
    }

    public boolean isPushCall() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_OFFLINE_PUSH_CALL, false);
    }

    public void setGroupsSynced(boolean synced) {
        editor.putBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, synced);
        editor.apply();
    }

    public boolean isGroupsSynced() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_GROUPS_SYNCED, false);
    }

    public void setCurrentUserName(String username) {
        editor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username);
        editor.apply();
    }

    public String getCurrentUsername() {
        return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
    }


    /**
     * 通知是否显示详情
     *
     * @param isShowNotifyDetails true/false(默认)
     */
    public void setShowNotifyDetails(boolean isShowNotifyDetails) {
        editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION_SHOW_DETAILS, isShowNotifyDetails);
        editor.apply();
    }

    public boolean getShowNotifyDetails() {
        return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION_SHOW_DETAILS, false);
    }

    //缓存本次请求时间
    public void saveCachePreTime(long preTime) {
        editor.putLong(SHARED_KEY_CACHE_NAME_PRE_TIME, preTime).apply();
    }

    public long getCachePreTime() {
        return mSharedPreferences.getLong(SHARED_KEY_CACHE_NAME_PRE_TIME, 0);
    }

    public void setLastCacheUsersTime(long millonTime) {
        editor.putLong(SHARED_KEY_LAST_CACHE_USERS_TIME, millonTime).apply();
    }

    public long getLastCacheUsersTime() {
        return mSharedPreferences.getLong(SHARED_KEY_LAST_CACHE_USERS_TIME, 0);
    }

    public void setLastCacheOrgUsersTime(long millonTime) {
        editor.putLong(SHARED_KEY_LAST_CACHE_ORG_USERS_TIME, millonTime).apply();
    }

    public long getLastCacheOrgUsersTime() {
        return mSharedPreferences.getLong(SHARED_KEY_LAST_CACHE_ORG_USERS_TIME, 0);
    }

    public long getCacheGroupsTime() {
        return mSharedPreferences.getLong(SHARED_KEY_CACHE_GROUP_TIME, 0);
    }

    public void setCacheGroupsTime(long cacheTime) {
        editor.putLong(SHARED_KEY_CACHE_GROUP_TIME, cacheTime);
    }


    //清除
    public void clearAll() {
        editor.remove(SHARED_KEY_LOGIN_USER).remove(SHARED_KEY_LOGIN_APPKEY)
                .remove(SHARED_KEY_LAST_CACHE_USERS_TIME)
                .remove(SHARED_KEY_LAST_CACHE_ORG_USERS_TIME)
                .remove(SHARED_KEY_LAST_CACHE_USERNAME)
                .remove(SHARED_KEY_CACHE_NAME_PRE_TIME)
                .remove(SHARED_KEY_CACHE_GROUP_TIME).commit();
    }

    public void clearCacheTime() {
        editor.remove(SHARED_KEY_LAST_CACHE_USERS_TIME)
                .remove(SHARED_KEY_LAST_CACHE_USERNAME)
                .remove(SHARED_KEY_CACHE_NAME_PRE_TIME)
                .remove(SHARED_KEY_CACHE_GROUP_TIME).commit();
    }


    public String getLastCacheUsername() {
        return mSharedPreferences.getString(SHARED_KEY_LAST_CACHE_USERNAME, "");
    }

    public int getLastCacheLoginStatus() {
        return mSharedPreferences.getInt(SHARED_KEY_LAST_LOGIN_STATUS, -1);
    }

    public void setServerIpConfig(String config){
        editor.putString(SHARED_KEY_SERVER_IP_CONFIG, config).apply();
    }

    public String getServerIpConfig() {
        return mSharedPreferences.getString(SHARED_KEY_SERVER_IP_CONFIG, "");
    }
}
