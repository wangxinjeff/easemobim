package com.hyphenate.easemob.imlibs.mp.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author liyuzhao
 * email:liyuzhao@easemob.com
 * date: 11/12/2018
 */
public class PrefsUtil {
    private final String PREFERENCE_NAME = "oa_saveInfo";
    private static PrefsUtil instance = new PrefsUtil();
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;
    private Context mContext;
    private static final String SHARED_KEY_SESSION = "session";
    private static final String SHARED_KEY_CURRENTUSER_USERNAME = "SHARED_KEY_CURRENTUSER_USERNAME";
    private static final String SHARED_KEY_LOGIN_All_USER_ENTITY = "login_user_all_entity";
    private static final String SHARED_KEY_LOGIN_USER = "login_user";
    private static final String SHARED_KEY_LOGIN_APPKEY = "login_appkey";
    private static final String SHARED_KEY_LOGIN_USERNAME = "login_username";
    private static final String SHARED_KEY_LOGIN_NAME_NICK = "login_nick";
    private static final String SHARED_KEY_LOGIN_NAME_AVATAR = "login_avatar";

    private static final String SHARED_KEY_LOGIN_NAME_USER_ID = "login_user_id";
    private static final String SHARED_KEY_LOGIN_NAME_EASE_NAME = "login_ease_name";
    private static final String SHARED_KEY_LOGIN_NAME_EASE_PWD = "login_ease_pwd";
    private static final String SHARED_KEY_LOGIN_NAME_EMAIL = "login_email";
    private static final String SHARED_KEY_LOGIN_NAME_GENDER = "login_gender";
    private static final String SHARED_KEY_LOGIN_NAME_PHONE = "login_phone";
    private static final String SHARED_KEY_LOGIN_NAME_TEL_PHONE = "login_telphone";
    private static final String SHARED_KEY_LOGIN_NAME_ORGANIZATION_ID = "login_organization_id";
    private static final String SHARED_KEY_LOGIN_NAME_TENANT_ID = "login_tenant_id";


    private static final String SHARED_KEY_IM_SERVER = "im_server";
    private static final String SHARED_KEY_REST_SERVER = "rest_server";
    private static final String SHARED_KEY_WHITEBOARD_SERVER = "whiteboard_server";
    private static final String SHARED_KEY_CCS_SERVER = "ccs_server";
    private static final String SHARED_KEY_IS_HTTPS = "is_https";
    private static final String SHARED_KEY_APPKEY = "appkey";
    private static final String SHARED_KEY_IPV4_SERVER = "ipv4_server";
    private static final String SHARED_KEY_IPV6_SERVER = "ipv6_server";

    private static final String SHARED_KEY_TIME_STAMP = "time_stamp";

    private PrefsUtil() {
    }

    public static PrefsUtil getInstance() {
        return instance;
    }

    public void init(Context context) {
        if (context != null) {
            this.mContext = context;
        }
        if (mSharedPreferences != null) return;
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
    }


    public void setCurrentUserName(String username) {
        editor.putString(SHARED_KEY_CURRENTUSER_USERNAME, username);
        editor.apply();
    }

    public String getCurrentUsername() {
        return mSharedPreferences.getString(SHARED_KEY_CURRENTUSER_USERNAME, null);
    }

    public void setSession(String session) {
        editor.putString(SHARED_KEY_SESSION, session);
        editor.commit();
    }

    public String getSession() {
        return mSharedPreferences.getString(SHARED_KEY_SESSION, null);
    }

    public void setAppkey(String appkey) {
        editor.putString(SHARED_KEY_LOGIN_APPKEY, appkey).commit();
    }

    public String getAppkey() {
        return mSharedPreferences.getString(SHARED_KEY_LOGIN_APPKEY, null);
    }

    public void setLoginUser(int userId, String easemobName, String easemobPwd, String username, String realName, String image, String gender, String phone, String telephone, String email, int tenantId, int organizationId) {
        editor.putInt(SHARED_KEY_LOGIN_NAME_USER_ID, userId)
                .putString(SHARED_KEY_LOGIN_NAME_EASE_NAME, easemobName)
                .putString(SHARED_KEY_LOGIN_NAME_EASE_PWD, easemobPwd)
                .putString(SHARED_KEY_LOGIN_USERNAME, username)
                .putString(SHARED_KEY_LOGIN_NAME_NICK, realName)
                .putString(SHARED_KEY_LOGIN_NAME_AVATAR, image)
                .putString(SHARED_KEY_LOGIN_NAME_GENDER, gender)
                .putString(SHARED_KEY_LOGIN_NAME_PHONE, phone)
                .putString(SHARED_KEY_LOGIN_NAME_TEL_PHONE, telephone)
                .putString(SHARED_KEY_LOGIN_NAME_EMAIL, email)
                .putInt(SHARED_KEY_LOGIN_NAME_TENANT_ID, tenantId)
                .putInt(SHARED_KEY_LOGIN_NAME_ORGANIZATION_ID, organizationId)
                .commit();
    }

    public void setLoginUser(String loginUser) {
        editor.putString(SHARED_KEY_LOGIN_USER, loginUser);
        editor.commit();
    }

    public void setLoginAllUserEntity(String loginUser) {
        editor.putString(SHARED_KEY_LOGIN_All_USER_ENTITY, loginUser);
        editor.commit();
    }

    public String getLoginAllEntity() {
        return mSharedPreferences.getString(SHARED_KEY_LOGIN_All_USER_ENTITY, null);
    }


    public String getLoginUser() {
        return mSharedPreferences.getString(SHARED_KEY_LOGIN_USER, null);
    }

    public String getSharedKeyLoginNameEaseName() {
        return mSharedPreferences.getString(SHARED_KEY_LOGIN_NAME_EASE_NAME, null);
    }

    public String getSharedKeyLoginNameEasePwd() {
        return mSharedPreferences.getString(SHARED_KEY_LOGIN_NAME_EASE_PWD, null);
    }

    public int getTenantId() {
        return mSharedPreferences.getInt(SHARED_KEY_LOGIN_NAME_TENANT_ID, 0);
    }


    public String getImServer() {
        return mSharedPreferences.getString(SHARED_KEY_IM_SERVER, null);
    }

    public void setImServer(String imServer) {
        editor.putString(SHARED_KEY_IM_SERVER, imServer);
        editor.commit();
    }

    public String getRestServer() {
        return mSharedPreferences.getString(SHARED_KEY_REST_SERVER, null);
    }

    public void setRestServer(String restServer) {
        editor.putString(SHARED_KEY_REST_SERVER, restServer);
        editor.commit();
    }

    public String getWhiteBoardServer() {
        return mSharedPreferences.getString(SHARED_KEY_WHITEBOARD_SERVER, null);
    }

    public void setWhiteBoardServer(String whiteBoardServer) {
        editor.putString(SHARED_KEY_WHITEBOARD_SERVER, whiteBoardServer);
        editor.commit();
    }

    public String getCcsServer() {
        return mSharedPreferences.getString(SHARED_KEY_CCS_SERVER, null);
    }

    public void setCcsServer(String ccsServer) {
        editor.putString(SHARED_KEY_CCS_SERVER, ccsServer);
        editor.commit();
    }

    public String getAppkey1() {
        return mSharedPreferences.getString(SHARED_KEY_APPKEY, null);
    }

    public void setAppkey1(String ccsServer) {
        editor.putString(SHARED_KEY_APPKEY, ccsServer);
        editor.commit();
    }

    public String getIpv4Server() {
        return mSharedPreferences.getString(SHARED_KEY_IPV4_SERVER, null);
    }

    public void setIpv4Server(String ipv4Server) {
        editor.putString(SHARED_KEY_IPV4_SERVER, ipv4Server);
        editor.commit();
    }

    public String getIpv6Server() {
        return mSharedPreferences.getString(SHARED_KEY_IPV6_SERVER, null);
    }

    public void setIpv6Server(String ipv6Server) {
        editor.putString(SHARED_KEY_IPV6_SERVER, ipv6Server);
        editor.commit();
    }

    public boolean isHttps() {
        return mSharedPreferences.getBoolean(SHARED_KEY_IS_HTTPS, false);
    }

    public void setHttps(boolean isHttps) {
        editor.putBoolean(SHARED_KEY_IS_HTTPS, isHttps);
        editor.commit();
    }

    public long getTimeStamp() {
        return mSharedPreferences.getLong(SHARED_KEY_TIME_STAMP, 0);
    }

    public void setTimeStamp(long timeDiff) {
        editor.putLong(SHARED_KEY_TIME_STAMP, timeDiff);
        editor.apply();
    }
}
